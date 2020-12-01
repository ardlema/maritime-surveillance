package com.github.ardlema.alerts.config

import java.lang
import java.util.Properties
import java.util.concurrent.TimeUnit

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig

object KafkaStreamsConfig {

  def buildStreamsConfiguration(applicationName: String,
                                stateDir: String,
                                bootstrapServers: String,
                                schemaRegistryUrl: String): Properties = {
    val streamsConfiguration = new Properties()

    // Give the Streams application a unique name.
    // The name must be unique in the Kafka cluster against which the application is run.
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName)
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, applicationName + "-client")

    // Where to find Kafka broker(s).
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    streamsConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)

    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, stateDir)

    // Set to earliest so we don't miss any data that arrived in the topics before the process started
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    // Specify default (de)serializers for record keys and for record values.
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName())
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName())

    streamsConfiguration
  }

}
