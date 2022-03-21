package de.iteratec.konfi.rest

import de.iteratec.konfi.domain.model.Attendee
import de.iteratec.konfi.domain.model.Conference
import de.iteratec.konfi.rest.dto.AttendeeDto
import de.iteratec.konfi.rest.dto.ConferenceDto
import org.springframework.stereotype.Component

@Component
class DtoMapper {
    fun toModel(dto: ConferenceDto) = Conference(
        id = dto.id,
        name = dto.name!!,
        maxAttendees = dto.maxAttendees,
        startDate = dto.startDate!!,
        endDate = dto.endDate!!,
    )

    fun toDto(model: Conference) = ConferenceDto(
        id = model.id,
        name = model.name,
        maxAttendees = model.maxAttendees,
        startDate = model.startDate,
        endDate = model.endDate,
    )

    fun toModel(dto: AttendeeDto) = Attendee(
        firstName = dto.firstName!!,
        lastName = dto.lastName!!,
        email = dto.email!!,
    )

    fun toDto(model: Attendee) = AttendeeDto(
        firstName = model.firstName,
        lastName = model.lastName,
        email = model.email,
    )
}