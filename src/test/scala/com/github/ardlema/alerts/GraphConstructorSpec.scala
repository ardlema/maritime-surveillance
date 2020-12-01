package com.github.ardlema.alerts

import org.scalatest.funsuite.AnyFunSuite

class GraphConstructorSpec extends AnyFunSuite  {

  test("throw NoSuchElementException if an empty stack is popped") {
    val array = Array(1.0F, 2.0F, 3.0F, 7.5F, 2.0F)
    val maxIndex = GraphConstructor.maxIndex(array)
    assert(maxIndex == 3)
  }

}
