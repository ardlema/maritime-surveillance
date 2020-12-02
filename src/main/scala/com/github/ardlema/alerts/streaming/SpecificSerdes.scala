package com.github.ardlema.alerts.streaming

import java.util.Collections

import com.github.ardlema.alerts.config.KafkaConfig
import com.github.ardlema.alerts.model.avro.ElasticsearchMessage
import com.github.ardlema.alerts.tensorflow.ValuePredictionImageBytes
import com.github.fbascheper.kafka.connect.telegram.TgMessage
import com.github.jcustenborder.kafka.connect.model.Value
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde

trait SpecificSerdes {

  val serdeConfig =
    Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfig.KafkaSchemaRegistryUrl)
  val inputImageSerde: SpecificAvroSerde[Value] = new SpecificAvroSerde[Value]()
  val telegramMessageSerde: SpecificAvroSerde[TgMessage] = new SpecificAvroSerde[TgMessage]()
  val elasticsearchMessageSerde: SpecificAvroSerde[ElasticsearchMessage] = new SpecificAvroSerde[ElasticsearchMessage]()
  inputImageSerde.configure(serdeConfig, false)
  telegramMessageSerde.configure(serdeConfig, false)
  elasticsearchMessageSerde.configure(serdeConfig, false)

}
