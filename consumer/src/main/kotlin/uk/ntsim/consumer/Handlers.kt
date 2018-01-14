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

/**
 * 1st example - Sending messages using channel bindings.
 *
 * To receive messages, all we need to do is apply the [StreamListener]
 * annotation to any method and the message will be sent through it.
 */
@Component
final class UserMessageHandler {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @StreamListener(USER_CHANNEL)
  fun saveUser(user: UserMessage) {
    log.info("Saving User: $user")
  }

  @StreamListener(USER_CHANNEL)
  fun emailUser(user: UserMessage) {
    log.info("Emailing User: $user")
  }
}

/**
 * 2nd example - Sending messages through Processors/Transformers.
 *
 * Processors/transformers are intended to transform input message data
 * and then forward it onto an output destination. In this example,
 * we perform some slightly validation which received an [AddressMessage]
 * from the input channel and then wraps it in a [ValidatedAddressMessage]
 * to be outputted to another channel.
 */
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

/**
 * 2nd example - Sending messages through Processors/Transformers.
 *
 * This handler just receives the final [ValidatedAddressMessage] that
 * was transforemd by the [AddressMessageValidator] and pretends to
 * save it somewhere.
 */
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

/**
 * 3rd example - Using Reactor for reactive programming.
 *
 * Again, we can utilise Reactive Streams APIs via Reactor and
 * Spring Cloud Streams. In this instance, we can continue to use
 * the [StreamListener] annotation.
 *
 * Unlike the 1st example, we cannot seem to just consume the message and
 * terminate the stream. We have to define [Input] and [Output] channels
 * to form a  continuous stream of data instead. This is essentially the
 * same as the transformer/processor in the 2nd example.
 *
 * In this simplistic example, we just map over the [Flux] to a tax
 * amount to the values if required.
 */
@Component
final class PaymentMessageHandler {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)
  private val taxModifier = (1.2).toBigDecimal()

  @StreamListener
  @Output(TAXED_PAYMENT_CHANNEL)
  fun taxPayments(@Input(PAYMENT_CHANNEL) paymentStream: Flux<PaymentMessage>): Flux<PaymentMessage> {
    return paymentStream
      .doOnEach { log.info("Checking tax for Payment: $it") }
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
