package com.github.ardlema.alerts

import com.github.ardlema.alerts.config.{KafkaConfig, KafkaStreamsConfig}
import com.github.ardlema.alerts.streaming.SmugglingDetectorStreamsBuilder
import org.apache.log4j.Logger

import scala.util.control.NonFatal

object SmugglingDetector {

  val logger = Logger.getLogger(getClass().getName())

  val ImageInputTopic = KafkaConfig.ImageInputTopic
  val AlertsOutputTopic = KafkaConfig.AlertsOutputTopic
  val ElasticSearchOutputTopic = KafkaConfig.ElasticsearchOutputTopic

  def main(args : Array[String]) {
    val streamsConfiguration = KafkaStreamsConfig.buildStreamsConfiguration(
      "tensorflow-smuggling-detector", " /tmp",
      KafkaConfig.KafkaBootstrapServers,
      KafkaConfig.KafkaSchemaRegistryUrl)

    val streams = SmugglingDetectorStreamsBuilder.createStreams(streamsConfiguration)

    streams.cleanUp()
    streams.start()

    println("Smuggling detector stream microservice is running...")
    println("Getting events from the input Kafka Topic: " + ImageInputTopic + ". Sending alerts to the following Kafka Topic: " + AlertsOutputTopic)

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    sys.addShutdownHook {
      try {
        streams.close()
      } catch {
        case NonFatal(e) => println(s"During streams.close(), received: $e")
      }
    }
  }
}
