package uk.ntsim.consumer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.integration.annotation.Transformer
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
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
    if (address.isValid) {
      log.info("Saving Address: $address")
    } else {
      log.warn("Invalid Address received: $address")
    }
  }
}

@Component
class PaymentMessageHandler {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)
  private val taxModifier = (1.2).toBigDecimal()

  @StreamListener
  @Output(TAXED_PAYMENT_CHANNEL)
  fun taxPayments(@Input(PAYMENT_CHANNEL) paymentStream: Flux<PaymentMessage>): Flux<PaymentMessage> {
    paymentStream.subscribe { log.info("Checking tax for Payment: $it") }

    return paymentStream
      .map {
        if (!it.taxed) {
          PaymentMessage(
            amount = it.amount,
            taxed = true,

            taxedAmount = it.amount * taxModifier,
            vendor = it.vendor
          )
        } else it
      }
  }

  @StreamListener(TAXED_PAYMENT_CHANNEL)
  fun savePayments(payment: PaymentMessage) {
    log.info("Saving taxed Payment: $payment")
  }
}