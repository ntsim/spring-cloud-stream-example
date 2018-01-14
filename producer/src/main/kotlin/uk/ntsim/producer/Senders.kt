package uk.ntsim.producer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.reactive.FluxSender
import org.springframework.cloud.stream.reactive.StreamEmitter
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import uk.ntsim.*
import java.math.BigDecimal
import java.time.Duration

/**
 * 1st example - Sending messages using channel bindings.
 *
 * We can either inject in all of the Channels via the [ChannelBindings]
 * or directly inject in the required channel using the [Qualifier] annotation.
 *
 * Sending messages through these channels is quite simple as long as the
 * message is serializable. With Kotlin, this is pretty straightforward
 * using data classes.
 */
@Component
final class UserMessageSender(
  private val bindings: ChannelBindings,
  @Qualifier(USER_CHANNEL) private val channel: MessageChannel
) {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun sendUserMessage(message: UserMessage) {
    bindings.users().send(MessageBuilder.withPayload(message).build())
    log.info("Sent User message: $message")
  }

  fun sendUserMessageDirectly(message: UserMessage) {
    channel.send(MessageBuilder.withPayload(message).build())
    log.info("Sent User message directly with channel: $message")
  }
}

/**
 * 2nd example - Sending messages through Processors/Transformers.
 *
 * Refer to the Consumer application's [AddressMessage] handler.
 */
@Component
final class AddressMessageSender(private val bindings: ChannelBindings) {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun sendAddressMessage(message: AddressMessage) {
    bindings.addresses().send(MessageBuilder.withPayload(message).build())
    log.info("Sent Address message: $message")
  }
}

/**
 * 3rd example - Using Reactor for reactive programming.
 *
 * Spring Cloud Stream supports the Reactor project, allowing us to use
 * reactive stream APIs. This might not necessarily be required in all
 * instances, but can be particularly useful for things like
 * long-running asynchronous tasks.
 *
 * Reactor provides a [Flux] primitive, which represents a stream of
 * 1..N items, which we can do things such as [Flux.map], [Flux.filter]
 * (as you might find in the Java 8 Streams API).
 *
 * Spring Cloud Stream itself then provides the mechanism to output this
 * Flux via a [FluxSender].
 */
@Component
final class PaymentMessageSender {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  @StreamEmitter
  fun sendPaymentMessage(@Output(PAYMENT_CHANNEL) output: FluxSender) {
    val untaxedFlux = Flux
      .interval(Duration.ofSeconds(5L))
      .map {
        PaymentMessage(
          amount = (100.0).toBigDecimal(),
          taxed = false,
          taxedAmount = BigDecimal.ZERO,
          vendor = "Apple"
        )
      }

    val taxedFlux = Flux
      .interval(Duration.ofSeconds(1L))
      .map {
        PaymentMessage(
          amount = (200.0).toBigDecimal(),
          taxed = true,
          taxedAmount = (240.0).toBigDecimal(),
          vendor = "Orange"
        )
      }

    output.send(
      untaxedFlux
        .mergeWith(taxedFlux)
        .doOnEach { log.info("Sending Payment message: ${it.get()}") }
    )
  }
}
