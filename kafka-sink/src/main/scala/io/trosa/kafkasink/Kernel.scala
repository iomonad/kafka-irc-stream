package io.trosa.kafkasink

import akka.actor.ActorSystem

object Kernel extends App {

  val system = ActorSystem("kafka-sink")

}
