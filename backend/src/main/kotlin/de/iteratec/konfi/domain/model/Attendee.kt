package de.iteratec.konfi.domain.model

import javax.persistence.Embeddable

@Embeddable
class Attendee(
    var firstName: String,
    var lastName: String,
    var email: String
)