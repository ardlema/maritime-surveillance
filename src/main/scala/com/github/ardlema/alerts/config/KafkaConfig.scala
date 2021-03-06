package com.github.ardlema.alerts.config

object KafkaConfig {

  val ImageInputTopic = "image-events-spool"
  val AlertsOutputTopic = "smuggling-alerts-telegram"
  val ElasticsearchOutputTopic = "image-events-elasticsearch"
  val KafkaBootstrapServers = "localhost:9092"
  val KafkaSchemaRegistryUrl = "http://localhost:8081"
}
