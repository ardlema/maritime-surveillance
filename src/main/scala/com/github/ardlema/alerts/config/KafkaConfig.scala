package com.github.ardlema.alerts.config

object KafkaConfig {

  val ImageInputTopic = "image-events"
  val AlertsOutputTopic = "smuggling-alerts"
  val KafkaBootstrapServers = "localhost:9092"
  val KafkaSchemaRegistryUrl = "http://localhost:8081"
}
