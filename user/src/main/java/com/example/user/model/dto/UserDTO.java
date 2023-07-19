package com.example.user.model.dto;

import com.example.common.HasIdAndEmail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserDTO implements HasIdAndEmail {

    private String id;

    @Email
    @NotBlank
    @Size(min = 5, max = 128)
    private String email;

    @NotBlank
    @Size(min = 5, max = 128)
    private String firstName;

    @NotBlank
    @Size(max = 128)
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 5, max = 128)
    private String password;

    @NotBlank
    @Size(min = 5, max = 128)
    private String username;

    private boolean enabled;

    private String fileName;

    private List<String> roles;

    public void setEmail(String email) {
        this.email = StringUtils.hasText(email) ? email.toLowerCase() : null;
    }
}