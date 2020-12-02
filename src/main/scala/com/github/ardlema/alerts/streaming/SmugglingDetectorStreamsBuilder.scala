package com.github.ardlema.alerts.streaming

import java.nio.file.{Files, Paths}
import java.util.{Properties}

import com.github.ardlema.alerts.SmugglingDetector.{AlertsOutputTopic, ImageInputTopic}
import com.github.ardlema.alerts.tensorflow.{ImageClassifier, ValuePredictionImageBytes}
import com.github.fbascheper.kafka.connect.telegram.TgMessage
import com.github.jcustenborder.kafka.connect.model.Value
import com.github.ardlema.alerts.telegram.TelegramMessage
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Consumed, KStream, Produced}

object SmugglingDetectorStreamsBuilder extends SpecificSerdes {

  def createStreams(streamsConfiguration: Properties): KafkaStreams = {
    val builder = new StreamsBuilder()

    // In the subsequent lines we define the processing topology of the streams application
    val imageInputStream = builder.stream(ImageInputTopic)(Consumed.`with`(Serdes.String, inputImageSerde))
    val predictionStream = imageInputStream.mapValues(fileInformation => {
      val imageBytes = getImageBytesFromSourceFile(fileInformation)
      val prediction = ImageClassifier.classifyImage(imageBytes)
      println(s"Best match: ${prediction.label} (${prediction.probability}% likely)")
      ValuePredictionImageBytes(fileInformation, prediction, imageBytes)
    })

    val suspiciousObject: (String, ValuePredictionImageBytes) => Boolean = (_, valueAndPrediction) => valueAndPrediction.isSuspicious
    val notSuspiciousObject: (String, ValuePredictionImageBytes) => Boolean = (_, valueAndPrediction) => !valueAndPrediction.isSuspicious

    val detectionStreams = predictionStream.branch(suspiciousObject, notSuspiciousObject)

    val telegramAlertsStream: KStream[String, TgMessage] = detectionStreams(0).mapValues(valuePredictionImageBytes => {
      TelegramMessage.createMessageFromValuePrediction(valuePredictionImageBytes)
    })

    // Send the alerts to telegram topic (sink)
    telegramAlertsStream.to(AlertsOutputTopic)(Produced.`with`(Serdes.String, telegramMessageSerde))

    //Send the non suspicious elements to Elasticsearch to be tracked

    new KafkaStreams(builder.build(), streamsConfiguration)
  }

  def getImageBytesFromSourceFile(fileInformation: Value): Array[Byte] = {
    val imageFile = fileInformation.getSourceFile
    val pathImage = Paths.get(imageFile)
    Files.readAllBytes(pathImage)
  }
}
