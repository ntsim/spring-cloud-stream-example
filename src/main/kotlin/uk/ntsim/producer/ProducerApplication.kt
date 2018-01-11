package uk.ntsim.producer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import uk.ntsim.ADDRESS_CHANNEL
import uk.ntsim.AddressMessage
import uk.ntsim.USER_CHANNEL
import uk.ntsim.UserMessage

@SpringBootApplication
class ProducerApplication

@EnableBinding(ChannelBindings::class)
class MessageBindingConfig

fun main(args: Array<String>) {
  val context = SpringApplication.run(ProducerApplication::class.java, *args)
  val userMessageSender = context.getBean(UserMessageSender::class.java)
  userMessageSender.sendUserMessage(UserMessage("test@test.com", firstName = "John", lastName = "Doe", age = 20))
}

@Component
interface ChannelBindings {

  @Output(USER_CHANNEL)
  fun users(): MessageChannel

  @Output(ADDRESS_CHANNEL)
  fun addresses(): MessageChannel
}

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