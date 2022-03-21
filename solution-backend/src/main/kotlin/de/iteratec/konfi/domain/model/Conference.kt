package de.iteratec.konfi.domain.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Conference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    val name: String,
    val maxAttendees: Int = 200,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,

    @ElementCollection
    val attendees: MutableList<Attendee> = mutableListOf()
) {
    fun collidesWith(other: Conference): Boolean {
        return other.endDate >= startDate && other.startDate <= endDate
    }
}