package com.example.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class RegistrationDTO {
    @Email
    @NotBlank
    @Size(min = 5, max = 128)
    String email;

    @NotBlank
    @Size(min = 5, max = 128)
    String firstName;

    @NotBlank
    @Size(max = 128)
    String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 5, max = 128)
    String password;
}