package de.iteratec.konfi.rest.dto

import io.swagger.annotations.ApiModel
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class AttendeeDto(
    @get:Size(min = 2, max = 32)
    val firstName: String? = null,

    @get:Size(min = 2, max = 64)
    val lastName: String? = null,

    @get:NotNull
    @get:Email
    val email: String? = null
)