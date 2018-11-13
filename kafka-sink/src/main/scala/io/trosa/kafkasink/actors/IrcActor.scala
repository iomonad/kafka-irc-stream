package io.trosa.kafkasink.actors

import akka.Done
import akka.stream.scaladsl.Sink
import io.trosa.kafkasink.extensions.PooledStreamConnection
import io.trosa.kafkasink.models.{CreateConnection, DeleteConnection}

import scala.concurrent.Future

class IrcActor extends PooledStreamConnection {

  override def receive: Receive = {
    case server: CreateConnection =>
      add(server)
    case server: DeleteConnection =>
      remove(server)
  }

}
