package de.iteratec.konfi.rest.dto

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class AttendeeDto(
    @field:ApiModelProperty(example = "Max")
    val firstName: @Size(min = 2, max = 32) String,

    @field:ApiModelProperty(example = "Mustermann")
    val lastName: @Size(min = 2, max = 64) String,

    @field:NotNull
    @field:Email
    @field:ApiModelProperty(example = "maxi@i-love-kotlin.io", required = true)
    val email: String,
)
