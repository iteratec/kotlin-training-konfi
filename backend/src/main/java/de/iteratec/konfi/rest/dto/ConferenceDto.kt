package de.iteratec.konfi.rest.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiParam
import java.time.LocalDateTime
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class ConferenceDto {
    var id: Long? = null

    @field:NotNull
    @field:ApiModelProperty(example = "KotlinKonf", required = true)
    @field:Size(min = 3, max = 20)
    var name: String? = null

    @field:NotNull
    @field:ApiParam(example = "200")
    @field:ApiModelProperty(example = "200", required = true)
    var maxAttendees = 0

    @field:NotNull
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @field:ApiModelProperty(example = "2022-06-04 09:00", required = true)
    var startDate: LocalDateTime? = null

    @field:NotNull
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @field:ApiModelProperty(example = "2022-06-06 15:00", required = true)
    var endDate: LocalDateTime? = null

    @AssertTrue
    fun checkDates(): Boolean {
        return endDate!!.isAfter(startDate)
    }
}