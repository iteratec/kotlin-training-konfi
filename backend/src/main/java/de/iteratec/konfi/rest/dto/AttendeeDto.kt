package de.iteratec.konfi.rest.dto

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class AttendeeDto {
    @ApiModelProperty(example = "Max")
    var firstName: @Size(min = 2, max = 32) String? = null

    @ApiModelProperty(example = "Mustermann")
    var lastName: @Size(min = 2, max = 64) String? = null

    @NotNull
    @Email
    @ApiModelProperty(example = "maxi@i-love-kotlin.io", required = true)
    var email: String? = null
}