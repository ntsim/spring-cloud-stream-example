package uk.ntsim

data class UserMessage(
  val email: String,
  val firstName: String,
  val lastName: String,
  val age: Int
)

data class AddressMessage(
  val userId: Int,
  val address1: String,
  val address2: String,
  val city: String,
  val postcode: String
)

data class ValidatedAddressMessage(
  val isValid: Boolean,
  val data: AddressMessage
)