package de.iteratec.konfi.rest;

import de.iteratec.konfi.rest.dto.AttendeeDto;
import de.iteratec.konfi.rest.dto.ConferenceDto;
import de.iteratec.konfi.domain.ConferenceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/conference")
@Api(value = "Conference", tags = { "Conference" }, consumes = "application/json", produces = "application/json")
public class ConferenceEndpoint {

    private ConferenceService conferenceService;

    public ConferenceEndpoint(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @GetMapping
    @ApiOperation(value = "List all conferences")
    public List<ConferenceDto> findAll() {
        return conferenceService.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get a single conference")
    public ConferenceDto findOne(@PathVariable Long id) {
        return conferenceService.findOne(id);
    }

    @PostMapping
    @ApiOperation(value = "Create a new conference")
    public ConferenceDto create(@Valid @RequestBody ConferenceDto dto) {
        return conferenceService.create(dto);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete an existing conference")
    public void delete(@PathVariable Long id) {
        conferenceService.delete(id);
    }

    @GetMapping("/{id}/attendees")
    @ApiOperation(value = "Get a list of conference's attendees")
    public List<AttendeeDto> getAttendees(@PathVariable Long id) {
        return conferenceService.getAttendees(id);
    }

    @PostMapping("/{id}/attendees")
    @ApiOperation(value = "Register a new attendee")
    public void register(@PathVariable Long id, @Valid @RequestBody AttendeeDto attendee) {
        conferenceService.register(id, attendee);
    }

    @DeleteMapping("/{id}/attendees")
    @ApiOperation(value = "Remove an existing attendee")
    public void deregister(@PathVariable Long id, @Valid @RequestBody AttendeeDto attendee) {
        conferenceService.deregister(id, attendee);
    }
}
