package de.iteratec.konfi.domain.model

import java.time.LocalDateTime
import de.iteratec.konfi.domain.model.Attendee
import de.iteratec.konfi.domain.model.Conference
import java.util.ArrayList
import javax.persistence.*

@Entity
class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    var maxAttendees = 0
    var startDate: LocalDateTime? = null
    var endDate: LocalDateTime? = null

    @ElementCollection
    var attendees: List<Attendee> = ArrayList()
    fun collidesWith(other: Conference): Boolean {
        return other.endDate!!.compareTo(startDate) >= 0 && other.startDate!!.compareTo(endDate) <= 0
    }
}
