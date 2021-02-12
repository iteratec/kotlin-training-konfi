package de.iteratec.konfi.rest

import de.iteratec.konfi.domain.ConferenceService
import de.iteratec.konfi.rest.dto.AttendeeDto
import de.iteratec.konfi.rest.dto.ConferenceDto
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/conference")
@Api(value = "Conference", tags = ["Conference"], consumes = "application/json", produces = "application/json")
class ConferenceEndpoint(private val conferenceService: ConferenceService) {
    @GetMapping
    @ApiOperation(value = "List all conferences")
    fun findAll(): List<ConferenceDto> {
        return conferenceService.findAll()
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get a single conference")
    fun findOne(@PathVariable id: Long): ConferenceDto {
        return conferenceService.findOne(id)
    }

    @PostMapping
    @ApiOperation(value = "Create a new conference")
    fun create(@Valid @RequestBody dto: ConferenceDto?): ConferenceDto {
        return conferenceService.create(dto)
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete an existing conference")
    fun delete(@PathVariable id: Long) {
        conferenceService.delete(id)
    }

    @GetMapping("/{id}/attendees")
    @ApiOperation(value = "Get a list of conference's attendees")
    fun getAttendees(@PathVariable id: Long): List<AttendeeDto> {
        return conferenceService.getAttendees(id)
    }

    @PostMapping("/{id}/attendees")
    @ApiOperation(value = "Register a new attendee")
    fun register(@PathVariable id: Long, @Valid @RequestBody attendee: AttendeeDto?) {
        conferenceService.register(id, attendee)
    }

    @DeleteMapping("/{id}/attendees")
    @ApiOperation(value = "Remove an existing attendee")
    fun deregister(@PathVariable id: Long, @Valid @RequestBody attendee: AttendeeDto?) {
        conferenceService.deregister(id, attendee)
    }
}