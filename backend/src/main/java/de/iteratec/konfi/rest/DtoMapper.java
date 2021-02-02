package de.iteratec.konfi.rest;

import de.iteratec.konfi.rest.dto.AttendeeDto;
import de.iteratec.konfi.rest.dto.ConferenceDto;
import de.iteratec.konfi.domain.model.Attendee;
import de.iteratec.konfi.domain.model.Conference;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public Conference toModel(ConferenceDto dto) {
        Conference model = new Conference();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setMaxAttendees(dto.getMaxAttendees());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        return model;
    }

    public ConferenceDto toDto(Conference model) {
        ConferenceDto dto = new ConferenceDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setMaxAttendees(model.getMaxAttendees());
        dto.setStartDate(model.getStartDate());
        dto.setEndDate(model.getEndDate());
        return dto;
    }

    public Attendee toModel(AttendeeDto dto) {
        Attendee model = new Attendee();
        model.setFirstName(dto.getFirstName());
        model.setLastName(dto.getLastName());
        model.setEmail(dto.getEmail());
        return model;
    }

    public AttendeeDto toDto(Attendee model) {
        AttendeeDto dto = new AttendeeDto();
        dto.setFirstName(model.getFirstName());
        dto.setLastName(model.getLastName());
        dto.setEmail(model.getEmail());
        return dto;
    }
}
