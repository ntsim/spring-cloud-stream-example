package uk.ntsim.producer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component
import uk.ntsim.*

@SpringBootApplication
class ProducerApplication

@EnableBinding(ChannelBindings::class)
class MessageBindingConfig

fun main(args: Array<String>) {
  val context = SpringApplication.run(ProducerApplication::class.java, *args)

  val userMessageSender = context.getBean(UserMessageSender::class.java)
  userMessageSender.sendUserMessage(UserMessage("test@test.com", firstName = "John", lastName = "Doe", age = 20))

  val addressMessageSender = context.getBean(AddressMessageSender::class.java)
  addressMessageSender.sendAddressMessage(AddressMessage("1 Sheffield Way", "City Center", "Sheffield", "S1 SHF"))
  addressMessageSender.sendAddressMessage(AddressMessage("X", "X", "X", "XXX"))
}

@Component
interface ChannelBindings {

  @Output(USER_CHANNEL)
  fun users(): MessageChannel

  @Output(ADDRESS_VALIDATION_CHANNEL)
  fun addresses(): MessageChannel

  @Output(PAYMENT_CHANNEL)
  fun payments(): MessageChannel
}
