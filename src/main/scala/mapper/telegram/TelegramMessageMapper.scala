package mapper.telegram

import com.github.ardlema.alerts.model.avro.SerializableImage
import com.github.fbascheper.kafka.connect.telegram.{TgAttachment, TgMessage, TgMessageType, TgPhotoMessage}

object TelegramMessageMapper {

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
}
