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
  userMessageSender.sendUserMessageDirectly(UserMessage("test2@test.com", firstName = "Melissa", lastName = "Stewart", age = 40))

  val addressMessageSender = context.getBean(AddressMessageSender::class.java)
  addressMessageSender.sendAddressMessage(AddressMessage("1 Sheffield Way", "City Center", "Sheffield", "S1 SHF"))
  addressMessageSender.sendAddressMessage(AddressMessage("X", "X", "X", "XXX"))
}

/**
 * Spring Cloud Stream requires us to bind [MessageChannel]s in the
 * producing application. These channels are automatically generated
 * from the interface, so we do not need to be concerned about their
 * implementation. This is the useful abstraction that decouples us
 * from the messaging implementation i.e. RabbitMQ or Kafka.
 */
@Component
interface ChannelBindings {

  @Output(USER_CHANNEL)
  fun users(): MessageChannel

  @Output(ADDRESS_VALIDATION_CHANNEL)
  fun addresses(): MessageChannel

  @Output(PAYMENT_CHANNEL)
  fun payments(): MessageChannel
}
