package client.model

import kotlinx.serialization.Serializable

@Serializable
class Attendee(
    val email: String,
    val firstName: String,
    val lastName: String,
)
