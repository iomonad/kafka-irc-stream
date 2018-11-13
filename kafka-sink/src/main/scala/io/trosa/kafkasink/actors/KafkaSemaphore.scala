package io.trosa.kafkasink.actors

import io.trosa.kafkasink.extensions.MQActorWithBootedConsumers
import io.trosa.kafkasink.models.{KafkaHrInput, KafkaMessage, KafkaIrcInput}

class KafkaSemaphore extends
  MQActorWithBootedConsumers[KafkaMessage] {

  override def receive: Receive = {
    case reload: KafkaHrInput =>
      log.info(s"Got new Hot Reload directive ${reload.msg}")
    case input: KafkaIrcInput =>
      log.info(s"Got new IRC input ${input.msg}")
    case record: KafkaMessage =>
      log.info(s"Got New record: ${record.msg}")
  }

}
