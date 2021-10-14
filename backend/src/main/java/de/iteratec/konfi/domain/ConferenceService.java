package de.iteratec.konfi.domain;

import de.iteratec.konfi.rest.dto.AttendeeDto;
import de.iteratec.konfi.rest.dto.ConferenceDto;
import de.iteratec.konfi.domain.model.ConferenceCollisionException;
import de.iteratec.konfi.domain.model.RegisterAttendeeException;
import de.iteratec.konfi.domain.model.Attendee;
import de.iteratec.konfi.domain.model.Conference;
import de.iteratec.konfi.rest.DtoMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class ConferenceService {

    private final ConferenceRepository repository;
    private final DtoMapper mapper;

    public ConferenceService(ConferenceRepository repository, DtoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ConferenceDto findOne(Long id) {
        requireNonNull(id);

        Conference conference = repository.getOne(id);
        return mapper.toDto(conference);
    }

    public List<ConferenceDto> findAll() {
        List<Conference> conferences = repository.findAll();
        return conferences.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ConferenceDto create(ConferenceDto dto) {
        requireNonNull(dto);

        Conference conference = mapper.toModel(dto);
        List<Conference> collidingConferences = findCollidingConferences(conference);
        if (!collidingConferences.isEmpty()) {
            throw new ConferenceCollisionException("Other conference for this date invertal exists already");
        }

        Conference savedConference = repository.save(conference);
        return mapper.toDto(savedConference);
    }

    @Transactional
    public void delete(Long id) {
        requireNonNull(id);
        repository.deleteById(id);
    }

    public List<AttendeeDto> getAttendees(Long id) {
        requireNonNull(id);

        Conference conference = repository.getOne(id);
        return conference.getAttendees().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void register(Long id, AttendeeDto attendeeDto) {
        requireNonNull(id);
        requireNonNull(attendeeDto);

        Attendee attendee = mapper.toModel(attendeeDto);
        Conference conference = repository.getOne(id);

        if (conference.getAttendees().size() == conference.getMaxAttendees()) {
            throw new RegisterAttendeeException("The conference is full");
        }
        if (isAlreadyRegistered(attendee, conference)) {
            throw new RegisterAttendeeException("The attendee is already registered for this conference");
        }

        conference.getAttendees().add(attendee);
        repository.save(conference);
    }

    @Transactional
    public void deregister(Long id, AttendeeDto attendeeDto) {
        requireNonNull(id);
        requireNonNull(attendeeDto);

        Attendee attendee = mapper.toModel(attendeeDto);
        Conference conference = repository.getOne(id);
        conference.getAttendees().removeIf(a -> a.getEmail().equalsIgnoreCase(attendee.getEmail()));
        repository.save(conference);
    }

    private List<Conference> findCollidingConferences(Conference conference) {
        return repository.findByName(conference.getName()).stream()
                .filter(c -> c.collidesWith(conference))
                .collect(Collectors.toList());
    }

    private boolean isAlreadyRegistered(Attendee attendee, Conference conference) {
        return conference.getAttendees().stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(attendee.getEmail()));
    }
}
