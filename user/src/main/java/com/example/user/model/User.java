package com.example.user.model;

import com.example.user.HasIdAndEmail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "users_unique_email_idx")})
public class User extends BaseEntity implements HasIdAndEmail {

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    @Size(min = 5, max = 128)
    private String email;

    @Column(name = "first_name")
    @NotBlank
    @Size(min = 5, max = 128)
    private String firstName;

    @Column(name = "last_name")
    @NotBlank
    @Size(max = 128)
    private String lastName;

    @Column(name = "password", nullable = false)
    // https://stackoverflow.com/a/12505165/548473
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 5, max = 128)
    private String password;

    @Column(name = "enabled", nullable = false, columnDefinition = "bool default false")
    private boolean enabled;

    @Column(name = "activation_code")
    private String activationCode;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "user_roles_unique")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    public User(String email, String firstName, String lastName, String password, boolean enabled, String activationCode, Collection<Role> roles) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.enabled = enabled;
        this.activationCode = activationCode;
        setRoles(roles);
    }

    public User(String id, Instant createdAt, Instant modifiedAt, int version, String email, String firstName, String lastName, String password, boolean enabled, String activationCode, Collection<Role> roles) {
        super(id, createdAt, modifiedAt, version);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.enabled = enabled;
        this.activationCode = activationCode;
        setRoles(roles);
    }

    public void setEmail(String email) {
        this.email = StringUtils.hasText(email) ? email.toLowerCase() : null;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }
}