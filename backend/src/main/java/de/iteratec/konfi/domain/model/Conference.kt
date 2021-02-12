package de.iteratec.konfi.domain.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Conference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val name: String,
    val maxAttendees: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    // TODO: it seems hibernate requires a mutable list here, so we can't use listOf() or emptyList()
    @ElementCollection
    val attendees: MutableList<Attendee> = ArrayList(),
) {

    fun collidesWith(other: Conference): Boolean {
        return other.endDate >= startDate && other.startDate <= endDate
    }

}
