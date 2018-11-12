package io.trosa.kafkasink

import akka.actor.{ActorRef, ActorSystem, Props}
import io.trosa.kafkasink.actors.KafkaSemaphore

object Kernel extends App {

  val system = ActorSystem("kafka-sink")

  val kafka: ActorRef = system.actorOf(Props[KafkaSemaphore], "KafkaActor")
}
