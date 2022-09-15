package com.example.restaurant.model;

import com.example.common.HasIdAndEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;

@TypeDefs({
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        )
})
@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "restaurants")
public class Restaurant implements HasIdAndEmail {

    @Id
    @GeneratedValue(generator = "custom-generator",
            strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-generator",
            strategy = "com.example.restaurant.model.id.generator.BaseIdentifierGenerator")
    private String id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    protected Instant createdAt;

    @UpdateTimestamp
    protected Instant modifiedAt;

    @Version
    protected int version;

    @NotBlank
    @Size(min = 5, max = 128)
    String name;

    @NotBlank
    @Size(min = 5, max = 128)
    String cuisine;

    //  https://www.baeldung.com/java-hibernate-map-postgresql-array
    @Type(type = "string-array")
    @Column(
            name = "file_names",
            columnDefinition = "text[]"
    )
    String[] fileNames;

    @NotBlank
    @Email
    @Size(min = 5, max = 128)
    String email;

    @NotBlank
    @Size(min = 5, max = 128)
    String address;

    @NotBlank
    @Size(min = 5, max = 128)
    @Pattern(regexp = "([+]*[0-9]{1,4}\\s?[(]*\\d[0-9]{2,4}[)]*\\s?\\d{3}[-]*\\d{2}[-]*\\d{2})"
            , message = "Please fill the phone number in format +1 (234) 567-89-10")
    String phoneNumber;

    @NotBlank
    @Size(min = 5, max = 128)
    String website;

    public String id() {
        Assert.notNull(id, "Entity must have id");
        return id;
    }

    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    public void setEmail(String email) {
        this.email = StringUtils.hasText(email) ? email.toLowerCase() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(ProxyUtils.getUserClass(o))) {
            return false;
        }
        Restaurant that = (Restaurant) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}