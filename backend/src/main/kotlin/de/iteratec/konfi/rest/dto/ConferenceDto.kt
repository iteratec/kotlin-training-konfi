package de.iteratec.konfi.rest.dto

import java.time.LocalDateTime
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

class ConferenceDto(
    val id: Long? = null,

    @get:NotNull
    @get:Size(min = 3, max = 20)
    val name: String? = null,

    @get:NotNull
    @get:Positive
    val maxAttendees: Int = 0,

    @get:NotNull
    val startDate: LocalDateTime? = null,

    @get:NotNull
    val endDate: LocalDateTime? = null
) {
    @AssertTrue
    fun checkDates(): Boolean {
        return endDate?.isAfter(startDate) ?: false
    }
}