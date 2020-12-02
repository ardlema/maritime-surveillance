package com.github.ardlema.alerts.elasticsearch

import com.github.ardlema.alerts.model.avro.ElasticsearchMessage
import com.github.ardlema.alerts.tensorflow.ValuePredictionImageBytes

object ElasticsearchMessageBuilder {

  def createMessageFromValuePrediction(predictionValue: ValuePredictionImageBytes): ElasticsearchMessage = {
    val elasticSearchMessage = new ElasticsearchMessage()
    val latitudeAndLongitude = s"""${predictionValue.value.getLatitude},${predictionValue.value.getLongitude}"""
    elasticSearchMessage.setLatitudelongitude(latitudeAndLongitude)
    elasticSearchMessage.setLocationname(predictionValue.value.getLocation)
    elasticSearchMessage.setPredictionlabel(predictionValue.prediction.label)
    elasticSearchMessage.setProbability(predictionValue.prediction.probability)
    elasticSearchMessage.setTimestamp(predictionValue.value.getTimestamp)
    elasticSearchMessage
  }
}
