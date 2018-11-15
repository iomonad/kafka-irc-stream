package io.trosa.kafkasink.extensions

import akka.NotUsed
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink, Source}
import io.trosa.kafkasink.models.KafkaHrInput
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.duration._

/**
  * MQActor base cake trait extension with
  * wrapped connection configuration.
  * */
trait MQActor[A] extends CommonActor {

  /**
    * @note Single node used here. The config
    *       is shared with the trait.
    * */
  final lazy val kafkaconfig: ConsumerSettings[Array[Byte], String] =
    ConsumerSettings(system,
      new ByteArrayDeserializer,
      new StringDeserializer)
      .withBootstrapServers("dev01-hbm-kfk01.aws.cpdev.local/:9092")
      .withGroupId("group1")
      .withCloseTimeout(5 second)

}

/**
  * MQActor base trait extensions with defined consumers
  * (lazy).
  * */

trait MQActorWithConsumers[A] extends MQActor[A] {

  private final val hrtopic: String =
    config.getString("kafka.source_topic")

  /**
    * @note Use actor as sink.
    * */
  lazy val actorReloadSink: Sink[KafkaHrInput, NotUsed] =
    Sink.actorRef(self, NotUsed)

  /**
    * Hot Reload topic listener Akka Stream Source source.
    * @note should be started in preStart hook.
    * */
  lazy val hrconsumer: Source[KafkaHrInput, Consumer.Control] = Consumer
      .plainSource(kafkaconfig, Subscriptions.topics(hrtopic))
      .via(Flow[ConsumerRecord[Array[Byte], String]].map { x =>
        KafkaHrInput(x.key, x.value)
      })

}

trait MQActorWithBootedConsumers[A]
  extends MQActorWithConsumers[A] {

  /**
    * @note Boot up Subscribers and publishers
    * */
  override def preStart (): Unit = {
    hrconsumer.runWith(actorReloadSink)
  }
}
