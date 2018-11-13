package io.trosa.kafkasink.extensions

import java.net.InetSocketAddress

import akka.actor.ActorRef
import akka.io.IO
import akka.pattern.ask
import akka.io.Tcp._
import akka.stream.scaladsl.{MergeHub, Source, Tcp}
import akka.util.{ByteString, Timeout}
import io.trosa.kafkasink.models._

import scala.concurrent.duration._

/***
  *
  * @note commons IRC cake extension
  */
trait IrcCommons extends CommonActor {


}


/***
  * @note base StreamHub
  */
trait PooledStreamConnection extends IrcCommons {



}

/***
  * @note connection trait that represent
  *       one TCP socket on the IRC server.
  */
abstract class IrcConnection(server: InetSocketAddress, listener: ActorRef) extends IrcCommons {

  implicit val timeout = Timeout(5 seconds)

  private val manager: ActorRef = IO(Tcp)

  override def preStart (): Unit = {
    manager ! Connect(server)
  }

  override def receive: Receive = {
    case CommandFailed(_: Connect) =>
      (listener ? IrcConnectionFailed) andThen {
        case _ => context stop self
      }

    case c @ Connected(`server`, local) =>
      (listener ? c) andThen {
        case _ =>
          val conn = sender
          conn ? Register(self)
          context become {
            case data: ByteString => conn ? Write(data)
            case CommandFailed(w: Write) => listener ? IrcBufferFull(w)
            case Received(data: ByteString) =>
              listener ? ServerInput(data, IrcServer(server.getHostString))
            case _: ConnectionClosed => context stop self

            case CloseConnection => context stop self
          }
      }
  }

}
