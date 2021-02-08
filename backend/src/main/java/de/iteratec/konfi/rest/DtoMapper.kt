package de.iteratec.konfi.rest

import de.iteratec.konfi.rest.dto.ConferenceDto
import de.iteratec.konfi.domain.model.Conference
import de.iteratec.konfi.rest.dto.AttendeeDto
import de.iteratec.konfi.domain.model.Attendee
import org.springframework.stereotype.Component

@Component
class DtoMapper {
    fun toModel(dto: ConferenceDto): Conference {
        val model = Conference()
        model.id = dto.id
        model.name = dto.name
        model.maxAttendees = dto.maxAttendees
        model.startDate = dto.startDate
        model.endDate = dto.endDate
        return model
    }

    fun toDto(model: Conference): ConferenceDto {
        val dto = ConferenceDto()
        dto.id = model.id
        dto.name = model.name
        dto.maxAttendees = model.maxAttendees
        dto.startDate = model.startDate
        dto.endDate = model.endDate
        return dto
    }

    fun toModel(dto: AttendeeDto): Attendee {
        val model = Attendee()
        model.firstName = dto.firstName
        model.lastName = dto.lastName
        model.email = dto.email
        return model
    }

    fun toDto(model: Attendee): AttendeeDto {
        val dto = AttendeeDto()
        dto.firstName = model.firstName
        dto.lastName = model.lastName
        dto.email = model.email
        return dto
    }
}