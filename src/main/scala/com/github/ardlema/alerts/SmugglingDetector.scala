package com.github.ardlema.alerts

import com.github.ardlema.alerts.config.TopicConfig
import org.apache.log4j.Logger

object SmugglingDetector {
  
  def foo(x : Array[String]) = x.foldLeft("")((a,b) => a + b)
  
  def main(args : Array[String]) {

    val logger = Logger.getLogger(getClass().getName())

    val ImageInputTopic = TopicConfig.ImageInputTopic
    val AlertsOutputTopic = TopicConfig.AlertsOutputTopic

  }

}
