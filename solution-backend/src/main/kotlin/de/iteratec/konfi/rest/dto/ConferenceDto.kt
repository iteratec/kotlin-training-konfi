package de.iteratec.konfi.rest.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

class ConferenceDto(
    val id: Long? = null,

    @field:NotNull
    @field:Size(min = 3, max = 20)
    @field:ApiModelProperty(example = "KotlinKonf", required = true)
    val name: String? = null,

    @field:NotNull
    @field:Positive
    @field:ApiModelProperty(example = "200", required = true)
    val maxAttendees: Int = 0,

    @field:NotNull
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @field:ApiModelProperty(example = "2022-06-04 09:00", required = true)
    val startDate: LocalDateTime? = null,

    @field:NotNull
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @field:ApiModelProperty(example = "2022-06-06 15:00", required = true)
    val endDate: LocalDateTime? = null
) {
    @AssertTrue
    fun checkDates(): Boolean {
        return endDate?.isAfter(startDate) ?: false
    }
}