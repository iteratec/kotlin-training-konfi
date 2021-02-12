package de.iteratec.konfi.domain

import de.iteratec.konfi.domain.model.Attendee
import de.iteratec.konfi.domain.model.Conference
import de.iteratec.konfi.domain.model.ConferenceCollisionException
import de.iteratec.konfi.domain.model.RegisterAttendeeException
import de.iteratec.konfi.rest.DtoMapper
import de.iteratec.konfi.rest.dto.AttendeeDto
import de.iteratec.konfi.rest.dto.ConferenceDto
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
class ConferenceService(private val repository: ConferenceRepository, private val mapper: DtoMapper) {
    fun findOne(id: Long): ConferenceDto {
        Objects.requireNonNull(id)
        val conference = repository.getOne(id)
        return mapper.toDto(conference)
    }

    fun findAll(): List<ConferenceDto> {
        val conferences = repository.findAll()
        return conferences.stream().map { model: Conference? ->
            mapper.toDto(
                model!!
            )
        }.collect(Collectors.toList())
    }

    @Transactional
    fun create(dto: ConferenceDto?): ConferenceDto {
        Objects.requireNonNull(dto)
        val conference = mapper.toModel(dto!!)
        val collidingConferences = findCollidingConferences(conference)
        if (!collidingConferences.isEmpty()) {
            throw ConferenceCollisionException()
        }
        val savedConference = repository.save(conference)
        return mapper.toDto(savedConference)
    }

    @Transactional
    fun delete(id: Long) {
        Objects.requireNonNull(id)
        repository.deleteById(id)
    }

    fun getAttendees(id: Long): List<AttendeeDto> {
        Objects.requireNonNull(id)
        val (_, _, _, _, _, attendees) = repository.getOne(id)
        return attendees.stream()
            .map { model: Attendee? -> mapper.toDto(model!!) }
            .collect(Collectors.toList())
    }

    @Transactional
    fun register(id: Long, attendeeDto: AttendeeDto?) {
        Objects.requireNonNull(id)
        Objects.requireNonNull(attendeeDto)
        val attendee = mapper.toModel(attendeeDto!!)
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
    fun deregister(id: Long, attendeeDto: AttendeeDto?) {
        Objects.requireNonNull(id)
        Objects.requireNonNull(attendeeDto)
        val (_, _, email) = mapper.toModel(attendeeDto!!)
        val conference = repository.getOne(id)
        conference.attendees.removeIf { (_, _, email1) -> email1.equals(email, ignoreCase = true) }
        repository.save(conference)
    }

    private fun findCollidingConferences(conference: Conference): List<Conference> {
        return repository.findByName(conference.name).stream()
            .filter { c: Conference -> c.collidesWith(conference) }
            .collect(Collectors.toList())
    }

    private fun isAlreadyRegistered(attendee: Attendee, conference: Conference): Boolean {
        return conference.attendees.stream()
            .anyMatch { (_, _, email) -> email.equals(attendee.email, ignoreCase = true) }
    }
}