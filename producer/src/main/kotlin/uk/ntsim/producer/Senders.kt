package uk.ntsim.producer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.reactive.FluxSender
import org.springframework.cloud.stream.reactive.StreamEmitter
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import uk.ntsim.AddressMessage
import uk.ntsim.PAYMENT_CHANNEL
import uk.ntsim.PaymentMessage
import uk.ntsim.UserMessage
import java.math.BigDecimal
import java.time.Duration

@Component
final class UserMessageSender(private val bindings: ChannelBindings) {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun sendUserMessage(message: UserMessage) {
    bindings.users().send(MessageBuilder.withPayload(message).build())
    log.info("Sent User message: $message")
  }
}

@Component
final class AddressMessageSender(private val bindings: ChannelBindings) {

  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun sendAddressMessage(message: AddressMessage) {
    bindings.addresses().send(MessageBuilder.withPayload(message).build())
    log.info("Sent Address message: $message")
  }
}

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
