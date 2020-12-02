package com.github.ardlema.alerts.tensorflow

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

object ImageClassifier {

  //TensorFlow objects
  val modelDir = "src/main/resources/model/tensorflow"
  val pathGraph = Paths.get(modelDir, "tensorflow_inception_graph.pb")
  val graphDefinition = Files.readAllBytes(pathGraph)
  val pathModel = Paths.get(modelDir, "imagenet_comp_graph_label_strings.txt")
  val labels = Files.readAllLines(pathModel, Charset.forName("UTF-8"))

  def classifyImage(image: Array[Byte]): Prediction = {
    val tensor = GraphConstructor.constructAndExecuteGraphToNormalizeImage(image)
    val labelProbabilities = GraphConstructor.executeInceptionGraph(graphDefinition, tensor)
    val bestLabelIdx = GraphConstructor.maxIndex(labelProbabilities)
    val imageClassification = labels.get(bestLabelIdx)
    val probability = labelProbabilities(bestLabelIdx) * 100F
    Prediction(imageClassification, probability)
  }
}
