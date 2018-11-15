package io.trosa.kafkasink.extensions

import java.net.InetSocketAddress

import akka.{Done, NotUsed}
import akka.actor.ActorRef
import akka.event.Logging
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import io.trosa.kafkasink.models._

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}

/***
  *
  * @note commons IRC cake extension
  */
trait IrcCommons extends CommonActor {

  // Only for API change purposes

}

/***
  * @note base StreamHub
  */
trait PooledStreamConnection extends IrcCommons {

  /***
    * Connection switches to manage hub.
    */
  private val switches: mutable.Map[String, KillSwitch] =
    new mutable.HashMap[String, KillSwitch]()

  val consumer: Sink[Any, Future[Done]] = Sink.foreach(println)

  /**
    * MergeHub for aggregated message.
    * */
  val hub: RunnableGraph[Sink[ServerInput, NotUsed]] =
    MergeHub.source[ServerInput](perProducerBufferSize = 16)
      .to(consumer)

  /**
    * Internal function to add connection in the
    * mergeHub and memoize it into switch map.
    * */
  def add(connection: CreateConnection): Unit = {
    import GraphDSL.Implicits._

    val server = connection.server.getHostString

    val graph: Graph[SourceShape[ServerInput], NotUsed] =
      GraphDSL.create() { implicit builder =>

      /**
        * Source connection from server
        */
      val source = builder.add(Source(1 to 1000000).map(ByteString(_)))

      /**
        * Wrap bytestring into convenient object.
        */
      val converter: FlowShape[ByteString, ServerInput] = builder.add(Flow[ByteString]
        .map { x =>
          ServerInput(x, IrcServer(connection.server.getHostString))
        })

      /**
        * Output Pipe
        * */
      val pipe: FlowShape[ServerInput, ServerInput] =
        builder.add(Flow[ServerInput]
          .log("connection", x => s"Got new input from IRC source: ${x.message}")
          .withAttributes(Attributes.logLevels(onElement = Logging.DebugLevel)))

      source ~> converter ~> pipe

      SourceShape(pipe.out)
    }

    /***
      * @note our unique killswitch, used to manage the
      *       MergeHub input.
      */
    val switch: UniqueKillSwitch = Source.fromGraph(graph)
      .viaMat(KillSwitches.single)(Keep.right)
      .to(hub.run).run

    switches.put(server, switch)
  }

  /**
    * Internal function to add connection in the
    * mergeHub and memoize it into switch map.
    * */
  def remove(connection: DeleteConnection): Unit = {
    val server = connection.server.getHostString

    switches.get(server) match {
      case Some(switch) =>
        switch.shutdown()
        switches.remove(server)
          log.info(s"Killed Switch connection: ${server} from MergeHub.")
      case None =>
        log.warning(s"""The switch for the server " $server " don't exists. Skipping.""")
    }
  }

}

/***
  * @note connection trait that represent
  *       one TCP socket on the IRC server.
  */
abstract class IrcConnection(server: InetSocketAddress, listener: ActorRef) extends IrcCommons {

}
