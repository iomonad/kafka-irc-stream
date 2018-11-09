package io.trosa.kafkasink

package object models {

  sealed trait KafkaModels[A]

  /**
    * Meta kafka message type
    * @note default type exist.
    *       here we simply define this class
    *       as a wrapper for Akka's PF matcher.
    * */
  case class KafkaMessage(byte: Array[Byte], msg: String)
    extends KafkaModels[String]

}
