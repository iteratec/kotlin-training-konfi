package de.iteratec.konfi.domain.model

class ConferenceCollisionException : RuntimeException()
class RegisterAttendeeException(message: String?) : RuntimeException(message)