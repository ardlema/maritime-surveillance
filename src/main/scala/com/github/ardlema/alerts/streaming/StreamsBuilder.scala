package com.github.ardlema.alerts.streaming

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import java.util.{Collections, Properties}

import com.github.ardlema.alerts.SmugglingDetector.{AlertsOutputTopic, ImageInputTopic}
import com.github.ardlema.alerts.config.KafkaConfig
import com.github.ardlema.alerts.model.avro.SerializableImage
import com.github.ardlema.alerts.tensorflow.GraphConstructor
import com.github.fbascheper.kafka.connect.telegram.TgMessage
import com.github.jcustenborder.kafka.connect.model.Value
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import com.github.ardlema.alerts.telegram.TelegramMessageMapper
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.scala.kstream.{Consumed, KStream, Produced}

object StreamsBuilder {

  def createStreams(streamsConfiguration: Properties): KafkaStreams = {
    val builder = new StreamsBuilder()

    // Create TensorFlow object
    val modelDir = "src/main/resources/model/tensorflow"
    val pathGraph = Paths.get(modelDir, "tensorflow_inception_graph.pb")
    val graphDefinition = Files.readAllBytes(pathGraph)
    val pathModel = Paths.get(modelDir, "imagenet_comp_graph_label_strings.txt")
    val labels = Files.readAllLines(pathModel, Charset.forName("UTF-8"))
    val serdeConfig =
      Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfig.KafkaSchemaRegistryUrl)
    val telegramMessageSerde = new SpecificAvroSerde[TgMessage]()
    val inputImageSerde = new SpecificAvroSerde[Value]()
    telegramMessageSerde.configure(serdeConfig, false)
    inputImageSerde.configure(serdeConfig, false)
    // In the subsequent lines we define the processing topology of the streams application
    val imageInputLines = builder.stream(ImageInputTopic)(Consumed.`with`(Serdes.String, inputImageSerde))
    val telegramPhotoMessage: KStream[String, TgMessage] = imageInputLines.mapValues(fileInformation => {
      val imageFile = fileInformation.getSourceFile
      val pathImage = Paths.get(imageFile)
      val imageBytes = Files.readAllBytes(pathImage)
      val image = GraphConstructor.constructAndExecuteGraphToNormalizeImage(imageBytes)
      val labelProbabilities = GraphConstructor.executeInceptionGraph(graphDefinition, image)
      val bestLabelIdx = GraphConstructor.maxIndex(labelProbabilities)
      val imageClassification = labels.get(bestLabelIdx)
      val probability = labelProbabilities(bestLabelIdx) * 100F
      val imageProbability = probability.toString
      println(s"Best match: $imageClassification ($imageProbability% likely)")
      val serImage = new SerializableImage(imageClassification.toString, ByteBuffer.wrap(imageBytes))
      TelegramMessageMapper.photoMessage(serImage, telegramMessageCaption(imageClassification, fileInformation))
    })

    // Send the alerts to telegram topic (sink)
    telegramPhotoMessage.to(AlertsOutputTopic)(Produced.`with`(Serdes.String, telegramMessageSerde))

    new KafkaStreams(builder.build(), streamsConfiguration)
  }

  def telegramMessageCaption(imageType: String, fileInfo: Value) = {
    val time = fileInfo.getTimestamp
    val cameraLocation = fileInfo.getLocation
    s"""This $imageType has been detected by the camera installed in $cameraLocation at $time"""
  }
}
