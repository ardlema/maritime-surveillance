package com.github.ardlema.alerts.tensorflow

import com.github.jcustenborder.kafka.connect.model.Value

case class Prediction(label: String, probability: Float)

case class ValuePredictionImageBytes(value: Value, prediction: Prediction, imageBytes: Array[Byte]) {

  val suspiciousObjectLabels = List("speedboat")

  def isSuspicious(): Boolean = {
    suspiciousObjectLabels.contains(prediction.label)
  }
}
