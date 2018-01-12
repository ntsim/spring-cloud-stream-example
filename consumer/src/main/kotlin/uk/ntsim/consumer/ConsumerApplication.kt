package uk.ntsim.consumer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel
import uk.ntsim.ADDRESS_CHANNEL
import uk.ntsim.ADDRESS_VALIDATION_CHANNEL
import uk.ntsim.USER_CHANNEL

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

  @Input(ADDRESS_VALIDATION_CHANNEL)
  fun addressesToValidate(): SubscribableChannel

  @Input(ADDRESS_CHANNEL)
  fun addresses(): SubscribableChannel
}
