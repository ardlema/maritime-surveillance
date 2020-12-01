package com.github.ardlema.alerts

import java.util.Properties

import com.github.ardlema.alerts.config.{KafkaConfig, KafkaStreamsConfig}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder}
import org.apache.log4j.Logger

import scala.util.control.NonFatal

object SmugglingDetector {

  val logger = Logger.getLogger(getClass().getName())

  val ImageInputTopic = KafkaConfig.ImageInputTopic
  val AlertsOutputTopic = KafkaConfig.AlertsOutputTopic
  
  def main(args : Array[String]) {
    // Create TensorFlow objects
    //byte[] tfGgraphDef = FileUtils.readFile("tensorflow/model/saved_fine_tuned_model.pb");

    val streamsConfiguration = KafkaStreamsConfig.buildStreamsConfiguration(
      "tensorflow-smuggling-detector", " /tmp",
      KafkaConfig.KafkaBootstrapServers,
      KafkaConfig.KafkaSchemaRegistryUrl);

    val streams = createStreams(
      streamsConfiguration);

    streams.cleanUp();
    // start processing
    streams.start();
    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams

    sys.addShutdownHook {
      try {
        streams.close()
      } catch {
        case NonFatal(e) => println(s"During streams.close(), received: $e")
      }
    }
  }

  def createStreams(streamsConfiguration: Properties): KafkaStreams = {
    val builder = new StreamsBuilder()
    new KafkaStreams(builder.build(), streamsConfiguration)
  }

}
