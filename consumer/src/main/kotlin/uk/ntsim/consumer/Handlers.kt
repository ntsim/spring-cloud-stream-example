package uk.ntsim.consumer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.integration.annotation.Transformer
import org.springframework.stereotype.Component
import uk.ntsim.*

@Component
final class UserMessageHandler {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @StreamListener(USER_CHANNEL)
  fun saveUser(user: UserMessage) {
    log.info("Saving User: $user")
  }
}

@Component
final class AddressMessageValidator {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @Transformer(inputChannel = ADDRESS_VALIDATION_CHANNEL, outputChannel = ADDRESS_CHANNEL)
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
