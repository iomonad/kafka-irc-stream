package io.trosa.kafkasink

import java.net.InetSocketAddress

import akka.io.Tcp.Write
import akka.util.ByteString

package object models {

  sealed trait KafkaModels[A]

  /***
    * Meta kafka message type
    * @note default type exist.
    *       here we simply define this class
    *       as a wrapper for Akka's PF matcher.
    * */
  case class KafkaMessage(byte: Array[Byte], msg: String)
    extends KafkaModels[String]


  /***
    * @note Hot Reload message strategy
    *       used to manage IRC connections.
    * @param byte kafka Kinput
    * @param msg kafka Vinput
    */
  case class KafkaHrInput(byte: Array[Byte], msg: String)
    extends KafkaModels[String]

  /***
    * @note Aggregated message from stream hub.
    * @param byte kafka Kinput
    * @param msg kafka Vinput
    */
  case class KafkaIrcInput(byte: Array[Byte], msg: String)
    extends KafkaModels[String]


  /**
    * @note wrap server informations
    * */
  case class IrcServer(servername: String)

  /***
    * @note IRC Event Akka messages
    */
  sealed trait IrcServerEvent
  case object IrcConnectionFailed extends IrcServerEvent
  case class IrcBufferFull(w: Write) extends IrcServerEvent

  /***
    * @note IRC connections related directives
    */
  sealed trait IrcMethods
  case class CreateConnection(server: InetSocketAddress) extends IrcMethods
  case class DeleteConnection(server: InetSocketAddress) extends IrcMethods
  case object CloseConnection extends IrcMethods
  case class ServerInput(message: ByteString, server: IrcServer)

}
