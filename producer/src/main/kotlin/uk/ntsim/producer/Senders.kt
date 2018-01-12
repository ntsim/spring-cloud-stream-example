package uk.ntsim.producer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import uk.ntsim.AddressMessage
import uk.ntsim.UserMessage

@Component
class UserMessageSender(private val bindings: ChannelBindings) {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun sendUserMessage(message: UserMessage) {
    bindings.users().send(MessageBuilder.withPayload(message).build())
    log.info("Sent User message: $message")
  }
}

@Component
class AddressMessageSender(private val bindings: ChannelBindings) {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun sendAddressMessage(message: AddressMessage) {
    bindings.addresses().send(MessageBuilder.withPayload(message).build())
    log.info("Sent Address message: $message")
  }
}
