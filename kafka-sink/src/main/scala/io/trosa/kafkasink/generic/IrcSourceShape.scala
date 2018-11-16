package io.trosa.kafkasink.generic

import java.io.OutputStream
import java.net.{InetAddress, InetSocketAddress, Socket}

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.util.ByteString
import io.trosa.kafkasink.models.{CreateConnection, IrcServer, ServerInput}

import scala.io.BufferedSource
import scala.util.Random

/***
  * Custom IRC connection source with pong
  * response included. (WIP)
  *
  * @param server
  * @param name
  * @tparam T
  */
class IrcSourceShape[T](server: CreateConnection, name: Option[String])
  extends GraphStage[SourceShape[ServerInput]] {

  val out: Outlet[ServerInput] = Outlet("IRCconnection.out")

  override val shape: SourceShape[ServerInput] = SourceShape(out)

  override def createLogic (inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      private val setup: InetSocketAddress =
        server.server

      private val connection: Socket =
        new Socket(setup.getHostName, setup.getPort)

      setHandler(out, new OutHandler {
        override def onPull (): Unit = {

          lazy val input: Stream[ByteString] =
            new BufferedSource(connection.getInputStream)
              .getLines.map(ByteString(_, "UTF-8")).toStream

          val writer: OutputStream =
            connection.getOutputStream

          input.foreach { message =>
            if (message.utf8String.contains("PING")) {
              writer.write(ByteString("PONG").toArray)
            }
            push(out, ServerInput(message,
              IrcServer(name.getOrElse(Random.nextString(5)))))
          }
        }
      })
    }

}
