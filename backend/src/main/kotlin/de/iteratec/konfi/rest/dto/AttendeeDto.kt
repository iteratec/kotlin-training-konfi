package de.iteratec.konfi.rest.dto

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class AttendeeDto(
    @field:Size(min = 2, max = 32)
    @field:ApiModelProperty(example = "Max")
    val firstName: String? = null,

    @field:Size(min = 2, max = 64)
    @field:ApiModelProperty(example = "Mustermann")
    val lastName: String? = null,

    @field:NotNull
    @field:Email
    @field:ApiModelProperty(example = "maxi@i-love-kotlin.io", required = true)
    val email: String? = null
)