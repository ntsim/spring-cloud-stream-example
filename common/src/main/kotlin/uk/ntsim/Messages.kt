package uk.ntsim

import java.math.BigDecimal

data class UserMessage(
  val email: String,
  val firstName: String,
  val lastName: String,
  val age: Int
)

data class AddressMessage(
  val address1: String,
  val address2: String,
  val city: String,
  val postcode: String
)

data class ValidatedAddressMessage(
  val isValid: Boolean,
  val data: AddressMessage
)

data class PaymentMessage(
  val amount: BigDecimal,
  val taxed: Boolean,
  val taxedAmount: BigDecimal = (0.0).toBigDecimal(),
  val vendor: String
)
