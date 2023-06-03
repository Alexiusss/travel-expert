package com.example.user.model;

import com.example.common.HasIdAndEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.context.annotation.Profile;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "users_unique_email_idx")})
@Profile("!kc")
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

    @Column(name = "username")
    @NotBlank
    @Size(min = 5, max = 128)
    private String username;

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

    private String fileName;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "user_roles_unique")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    private Set<Role> roles;

    @CollectionTable(name = "user_subscriptions", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "subscriber_id")
    @ElementCollection(fetch = FetchType.LAZY)
    // https://stackoverflow.com/a/62848296
    @JoinColumn(name = "channel_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Set<String> subscribers;

    @CollectionTable(name = "user_subscriptions", joinColumns = @JoinColumn(name = "subscriber_id"))
    @Column(name = "channel_id")
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Set<String> subscriptions;

    public User(String email, String firstName, String lastName, String username, String password, boolean enabled, String activationCode, Collection<Role> roles) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.activationCode = activationCode;
        setRoles(roles);
    }

    public User(String id, Instant createdAt, Instant modifiedAt, int version, String email, String firstName, String lastName, String username, String password, boolean enabled, String activationCode, Collection<Role> roles) {
        super(id, createdAt, modifiedAt, version);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
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