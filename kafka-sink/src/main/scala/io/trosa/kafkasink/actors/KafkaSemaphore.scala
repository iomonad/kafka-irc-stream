package io.trosa.kafkasink.actors

import io.trosa.kafkasink.meta.MQActorWithBootedConsumers
import io.trosa.kafkasink.models.KafkaMessage

class KafkaSemaphore extends
  MQActorWithBootedConsumers[KafkaMessage] {

  override def receive: Receive = {
    case record: KafkaMessage =>
      log.info(s"Got New record: ${record.msg}")
  }

}
