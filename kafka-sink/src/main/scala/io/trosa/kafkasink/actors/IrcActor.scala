package io.trosa.kafkasink.actors

import io.trosa.kafkasink.extensions.PooledStreamConnection
import io.trosa.kafkasink.models.{CreateConnection, DeleteConnection}

class IrcActor extends PooledStreamConnection {

  override def receive: Receive = {
    case server: CreateConnection =>
      add(server)
    case server: DeleteConnection =>
      remove(server)
  }

}
