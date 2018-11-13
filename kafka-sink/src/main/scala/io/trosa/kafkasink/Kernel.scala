package io.trosa.kafkasink

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props}
import io.trosa.kafkasink.actors.{IrcActor, KafkaSemaphore}
import io.trosa.kafkasink.models.CreateConnection

object Kernel extends App {

  val system = ActorSystem("kafka-sink")

  val kafka: ActorRef = system.actorOf(Props[KafkaSemaphore], "KafkaActor")
  val irc: ActorRef = system.actorOf(Props[IrcActor], "IrcConnection")


  irc ! CreateConnection(new InetSocketAddress("irc.freenode.net", 6667))
}
