package io.trosa.kafkasink.extensions

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

trait CommonActor extends Actor with ActorLogging {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materi: ActorMaterializer = ActorMaterializer()

  final lazy val config: Config =
    ConfigFactory.load

  /***
    * @note actoref from system should be
    *       instanciated here.
    */
}
