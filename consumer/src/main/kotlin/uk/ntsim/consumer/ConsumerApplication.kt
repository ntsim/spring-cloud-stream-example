package uk.ntsim.consumer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.integration.annotation.Transformer
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component
import uk.ntsim.*

fun main(args: Array<String>) {
  SpringApplication.run(ConsumerApplication::class.java, *args)
}

@SpringBootApplication
class ConsumerApplication

@EnableBinding(ChannelBindings::class)
class MessageBindingConfig

interface ChannelBindings {

  @Input(USER_CHANNEL)
  fun users(): SubscribableChannel

  @Input(ADDRESS_CHANNEL)
  fun addresses(): SubscribableChannel
}

@Component
final class UserMessageHandler {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @StreamListener(USER_CHANNEL)
  fun saveUser(user: UserMessage) {
    log.info("Saving User: $user")
  }
}

@Component
final class AddressMessageProcessor {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @Input(ADDRESS_VALIDATION_CHANNEL)
  @SendTo(ADDRESS_CHANNEL)
  fun validateAddress(address: AddressMessage): ValidatedAddressMessage {
    var isValid = true

    if (address.postcode == "XXX") {
      isValid = false
    }

    log.info("Validated Address: $address")

    return ValidatedAddressMessage(isValid, address)
  }
}

@Component
final class AddressMessageHandler {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @StreamListener(ADDRESS_CHANNEL)
  fun saveAddress(address: ValidatedAddressMessage) {
    log.info("Saving Address: $address")
  }
}