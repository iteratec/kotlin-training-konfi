package de.iteratec.konfi.rest.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AttendeeDto {

    @Size(min = 2, max = 32)
    @ApiModelProperty(example = "Max")
    private String firstName;

    @Size(min = 2, max = 64)
    @ApiModelProperty(example = "Mustermann")
    private String lastName;

    @NotNull
    @Email
    @ApiModelProperty(example = "maxi@i-love-kotlin.io", required = true)
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
