package de.iteratec.konfi.domain.model

import javax.persistence.Embeddable

@Embeddable
data class Attendee(
    var firstName: String,
    var lastName: String,
    var email: String,
)
