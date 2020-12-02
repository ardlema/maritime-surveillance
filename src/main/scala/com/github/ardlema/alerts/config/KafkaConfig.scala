package com.github.ardlema.alerts.config

object KafkaConfig {

  val ImageInputTopic = "image-events-spooldir-2"
  val AlertsOutputTopic = "smuggling-alerts"
  val KafkaBootstrapServers = "localhost:9092"
  val KafkaSchemaRegistryUrl = "http://localhost:8081"
}
