package com.github.ardlema.alerts.telegram

import java.nio.ByteBuffer

import com.github.ardlema.alerts.model.avro.SerializableImage
import com.github.ardlema.alerts.tensorflow.ValuePredictionImageBytes
import com.github.fbascheper.kafka.connect.telegram.{TgAttachment, TgMessage, TgMessageType, TgPhotoMessage}
import com.github.jcustenborder.kafka.connect.model.Value

object TelegramMessage {

  def photoMessage(image: SerializableImage, caption: String): TgMessage = {
    val attachment = new TgAttachment()
    attachment.setName(image.getName())
    attachment.setContents(image.getImageData())

    val photoMessage = new TgPhotoMessage()
    photoMessage.setCaption(caption)
    photoMessage.setPhoto(attachment)

    val tgMessage = new TgMessage()
    tgMessage.setMessageType(TgMessageType.PHOTO)
    tgMessage.setPhotoMessage(photoMessage)

    tgMessage
  }

  def createMessageFromValuePrediction(valuePredictionImageBytes: ValuePredictionImageBytes) = {
    val serializeImage = new SerializableImage(valuePredictionImageBytes.prediction.label,
      ByteBuffer.wrap(valuePredictionImageBytes.imageBytes))
    val messageCaption = TelegramMessage.telegramMessageCaption(valuePredictionImageBytes.prediction.label,
      valuePredictionImageBytes.value)
    TelegramMessage.photoMessage(serializeImage, messageCaption)
  }

  def telegramMessageCaption(imageType: String, fileInfo: Value) = {
    val time = fileInfo.getTimestamp
    val cameraLocation = fileInfo.getLocation
    s"""This $imageType has been detected by the camera installed in $cameraLocation at $time"""
  }
}
