# Spring Cloud Stream Example

This is an example implementation of a Spring Cloud Stream application demonstrating how it can be used with RabbitMQ.

## Setup

1. You will need to run RabbitMQ. You can simply start this up using Docker by running the following from the project 
   root:

   ```bash
   docker-compose up -d
   ```
    
2a. From IntelliJ (requires **Kotlin plugin >1.2** to be installed), run the applications in `consumer` 
    and `producer` - specifically `ConsumerApplication` and `ProducerApplication`.
   
   Order shouldn't matter, but `ConsumerApplication` may be ran first if there are any ordering issues.
   
2b. Alternatively from a terminal, open up two tabs then run the following tasks in each one respectively:

   ```bash
   # Terminal 1
   ./gradlew consumer:bootRun
   
   # Terminal 2
   ./gradlew producer:bootRun
   ```
    
   
## Observations

Once both applications are running, you should be able to observe in the logs something like the following:

`ProducerApplication`:

```
2018-01-14 23:34:05.287  INFO 4585 --- [           main] uk.ntsim.producer.UserMessageSender      : Sent User message: UserMessage(email=test@test.com, firstName=John, lastName=Doe, age=20)
2018-01-14 23:34:05.288  INFO 4585 --- [           main] uk.ntsim.producer.UserMessageSender      : Sent User message directly with channel: UserMessage(email=test2@test.com, firstName=Melissa, lastName=Stewart, age=40)
2018-01-14 23:34:05.290  INFO 4585 --- [           main] uk.ntsim.producer.AddressMessageSender   : Sent Address message: AddressMessage(address1=1 Sheffield Way, address2=City Center, city=Sheffield, postcode=S1 SHF)
2018-01-14 23:34:05.290  INFO 4585 --- [           main] uk.ntsim.producer.AddressMessageSender   : Sent Address message: AddressMessage(address1=X, address2=X, city=X, postcode=XXX)
2018-01-14 23:34:06.249  INFO 4585 --- [     parallel-2] uk.ntsim.producer.PaymentMessageSender   : Sending Payment message: PaymentMessage(amount=200.0, taxed=true, taxedAmount=240.0, vendor=Orange)
```

With `PaymentMessageSender` sending messages every second.
   
`ConsumerApplication`:

```
2018-01-14 23:34:05.305  INFO 4422 --- [-eOaPBG0ndIng-1] uk.ntsim.consumer.UserMessageHandler     : Saving User: UserMessage(email=test@test.com, firstName=John, lastName=Doe, age=20)
2018-01-14 23:34:05.305  INFO 4422 --- [-eOaPBG0ndIng-1] uk.ntsim.consumer.UserMessageHandler     : Emailing User: UserMessage(email=test@test.com, firstName=John, lastName=Doe, age=20)
2018-01-14 23:34:05.306  INFO 4422 --- [6ewbWTqAP3kpQ-1] u.n.consumer.AddressMessageValidator     : Validated Address: AddressMessage(address1=1 Sheffield Way, address2=City Center, city=Sheffield, postcode=S1 SHF)
2018-01-14 23:34:05.306  INFO 4422 --- [-eOaPBG0ndIng-1] uk.ntsim.consumer.UserMessageHandler     : Saving User: UserMessage(email=test2@test.com, firstName=Melissa, lastName=Stewart, age=40)
2018-01-14 23:34:05.307  INFO 4422 --- [-eOaPBG0ndIng-1] uk.ntsim.consumer.UserMessageHandler     : Emailing User: UserMessage(email=test2@test.com, firstName=Melissa, lastName=Stewart, age=40)
2018-01-14 23:34:05.307  INFO 4422 --- [6ewbWTqAP3kpQ-1] uk.ntsim.consumer.AddressMessageHandler  : Saving Address: ValidatedAddressMessage(isValid=true, data=AddressMessage(address1=1 Sheffield Way, address2=City Center, city=Sheffield, postcode=S1 SHF))
2018-01-14 23:34:05.308  INFO 4422 --- [6ewbWTqAP3kpQ-1] u.n.consumer.AddressMessageValidator     : Validated Address: AddressMessage(address1=X, address2=X, city=X, postcode=XXX)
2018-01-14 23:34:05.308  WARN 4422 --- [6ewbWTqAP3kpQ-1] uk.ntsim.consumer.AddressMessageHandler  : Invalid Address received: ValidatedAddressMessage(isValid=false, data=AddressMessage(address1=X, address2=X, city=X, postcode=XXX))
2018-01-14 23:34:06.255  INFO 4422 --- [56ke23gaa-3uQ-1] uk.ntsim.consumer.PaymentMessageHandler  : Checking tax for Payment: reactor.core.publisher.FluxDoOnEach$DoOnEachSubscriber@65371fe9
2018-01-14 23:34:06.256  INFO 4422 --- [56ke23gaa-3uQ-1] uk.ntsim.consumer.PaymentMessageHandler  : Saving taxed Payment: PaymentMessage(amount=200.0, taxed=true, taxedAmount=240.0, vendor=Orange)
```

With `PaymentMessageHandler` receiving messages every second.

If you open up the RabbitMQ interface at [http://localhost:15672](http://localhost:15672) 
(user: `guest`, password: `guest`), you should be able to observe that a bunch of channels, exchanges and queues have 
been setup automatically and messages are periodically being sent (specifically via the `payment-channel`).
   
## Explanation

Spring Cloud Stream essentially forms an abstraction layer that has the following benefits:

- Decouples application code from underlying message queue implementation i.e. we should be able to swap
  RabbitMQ with Apache Kafka relatively easily.
- It provides various annotations that allow us to remove boilerplate code e.g. channel binding configuration.
- Allows us to define more things in configuration (see consumer/producer `application.yml` files) instead of code.
- Has optional Reactive Streams support in the form of [Reactor](https://projectreactor.io/) or [RxJava](https://github.com/ReactiveX/RxJava).

In this example project, I have provided 3 examples which highlight some of these benefits:

1. Sending/receiving messages using channel bindings.
2. Sending/receiving messages through Processors/Transformers.
3. Sending/receiving messages using Reactor for reactive programming.

These examples are highlighted and explained more thoroughly in the respective application code docblocks.

- In `producer`, see `Senders`.
- In `consumer`, see `Handlers`.

## Other notes

- There were no particular issues with using Kotlin and Spring Cloud Stream itself. Serialisation of data classes
  works great!
- Reactor seems to offer quite a wide API, which will need some further skilling up to make the most out of it.