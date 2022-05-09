package de.iteratec.konfi.domain

import de.iteratec.konfi.domain.model.Attendee
import de.iteratec.konfi.domain.model.Conference
import de.iteratec.konfi.domain.model.ConferenceCollisionException
import de.iteratec.konfi.domain.model.RegisterAttendeeException
import de.iteratec.konfi.rest.DtoMapper
import de.iteratec.konfi.rest.dto.AttendeeDto
import de.iteratec.konfi.rest.dto.ConferenceDto
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ConferenceService(
    private val repository: ConferenceRepository,
    private val mapper: DtoMapper
) {

    fun get(id: Long): ConferenceDto {
        return repository.getOne(id).let { mapper.toDto(it) }
    }

    fun getAll(): List<ConferenceDto> {
        return repository.findAll().map { mapper.toDto(it) }
    }

    @Transactional
    fun create(dto: ConferenceDto): ConferenceDto {
        val conference = mapper.toModel(dto)
        val collidingConferences = findCollidingConferences(conference)
        if (collidingConferences.isNotEmpty()) {
            throw ConferenceCollisionException("Other conference exists for this date interval")
        }

        return repository.save(conference).let { mapper.toDto(it) }
    }

    @Transactional
    fun delete(id: Long) {
        repository.deleteById(id)
    }

    fun getAttendees(id: Long): List<AttendeeDto> {
        return repository.getOne(id).attendees.map { mapper.toDto(it) }
    }

    @Transactional
    fun register(id: Long, attendeeDto: AttendeeDto) {
        val attendee = mapper.toModel(attendeeDto)
        val conference = repository.getOne(id)

        if (conference.attendees.size == conference.maxAttendees) {
            throw RegisterAttendeeException("The conference is full")
        }

        if (isAlreadyRegistered(attendee, conference)) {
            throw RegisterAttendeeException("The attendee is already registered for this conference")
        }

        conference.attendees.add(attendee)
        repository.save(conference)
    }

    @Transactional
    fun deregister(id: Long, attendeeDto: AttendeeDto) {
        val attendee = mapper.toModel(attendeeDto)
        val conference = repository.getOne(id)
        conference.attendees.removeIf { it.email.equals(attendee.email, ignoreCase = true) }
        repository.save(conference)
    }

    private fun findCollidingConferences(conference: Conference): List<Conference> {
        return repository.findByName(conference.name)
            .filter { it.collidesWith(conference) }
            .filter { it.id != conference.id }
    }

    private fun isAlreadyRegistered(attendee: Attendee, conference: Conference): Boolean {
        return conference.attendees.any { it.email.equals(attendee.email, ignoreCase = true) }
    }
}
