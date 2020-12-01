package com.github.ardlema.alerts

import org.tensorflow.{DataType, Graph, Output, Session, Tensor}


object GraphConstructor {

  def constructAndExecuteGraphToNormalizeImage(imageBytes: Array[Byte]): Tensor = {
    // Graph construction: using the OperationBuilder class to construct a graph to
    // decode, resize and normalize a JPEG image.

    val graph = new Graph()
    val graphBuilder = GraphBuilder(graph)

    // Some constants specific to the pre-trained model at:
    // https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
    //
    // - The model was trained with images scaled to 224x224 pixels.
    // - The colors, represented as R, G, B in 1-byte each were
    // converted to
    // float using (value - Mean)/Scale.
    val height = 224
    val weight = 224
    val mean = 117f
    val scale = 1f

    // Since the graph is being constructed once per execution here, we
    // can use a constant for the
    // input image. If the graph were to be re-used for multiple input
    // images, a placeholder would
    // have been more appropriate.
    val input: Output = graphBuilder.constant("input", imageBytes)
    val output: Output = graphBuilder
      .div(graphBuilder.sub(
        graphBuilder.resizeBilinear(graphBuilder.expandDims(graphBuilder.cast(graphBuilder.decodeJpeg(input, 3), DataType.FLOAT),
          graphBuilder.constant("make_batch", 0)), graphBuilder.constant("size", Array[java.lang.Integer](height, weight))),
        graphBuilder.constant("mean", mean)), graphBuilder.constant("scale", scale))
    val session = new Session(graph)
    session.runner().fetch(output.op().name()).run().get(0)
  }

  def executeInceptionGraph(graphDef: Array[Byte], image: Tensor): Array[Float] = {
    val graph = new Graph()

    // Model loading: Using Graph.importGraphDef() to load a pre-trained Inception
    // model.
    graph.importGraphDef(graphDef)

    // Graph execution: Using a Session to execute the graphs and find the best
    // label for an image.
    val session = new Session(graph)
    val result = session.runner().feed("input", image).fetch("output").run().get(0)
    val rshape = result.shape()
    if (result.numDimensions() != 2 || rshape(0) != 1) {
      throw new RuntimeException(String.format(
        "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
        rshape.toString))
    }
    val nlabels = rshape(1)
    //TODO: REVIEW TIS!!!
    val arrayResult: Array[Array[Float]] = result.copyTo(Array(Array(1, nlabels)))
    arrayResult(0)
  }

  def testDummy(probabilities: Array[Float]): Integer = {
    1
  }

  def maxIndex(probabilities: Array[Float]): Integer = {
    probabilities.indices.max
  }
}

/* In the fullness of time, equivalents of the methods of this class should be auto-generated from the OpDefs linked
    into libtensorflow_jni.so. That would match what is done in other languages like Python, C++ and Go.
*/
case class GraphBuilder(graph: Graph) {

    def div(x: Output, y: Output): Output = {
      binaryOp("Div", x, y)
    }

    def sub(x: Output, y: Output): Output = {
      binaryOp("Sub", x, y)
    }

    def resizeBilinear(images: Output, size: Output): Output =  {
      binaryOp("ResizeBilinear", images, size)
    }

    def expandDims(input: Output, dim: Output): Output =  {
      binaryOp("ExpandDims", input, dim)
    }

    def cast(value: Output, dtype: DataType): Output = {
      graph.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0)
    }

    def decodeJpeg(contents: Output, channels: Long): Output =  {
      graph.opBuilder("DecodeJpeg", "DecodeJpeg").addInput(contents).setAttr("channels", channels).build()
        .output(0)
    }

    def constant(name: String, value: Any): Output = {
      val t = Tensor.create(value)
      graph.opBuilder("Const", name).setAttr("dtype", t.dataType()).setAttr("value", t).build().output(0)
    }

    def binaryOp(theType: String, in1: Output, in2: Output): Output = {
      graph.opBuilder(theType, theType).addInput(in1).addInput(in2).build().output(0)
    }
}
