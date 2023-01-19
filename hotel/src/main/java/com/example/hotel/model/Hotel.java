package com.example.hotel.model;

import com.example.common.HasIdAndEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
@ToString
@Table(name = "hotels")
public class Hotel implements HasIdAndEmail {

    @Id
    @GeneratedValue(generator = "custom-generator",
            strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-generator",
            strategy = "com.example.hotel.model.id.generator.BaseIdentifierGenerator")
    private String id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant modifiedAt;

    @Version
    private int version;

    @NotBlank
    @Size(min = 5, max = 128)
    private String name;

    @NotBlank
    @Size(min = 5, max = 128)
    private String address;

    @NotBlank
    @Email
    @Size(min = 5, max = 128)
    private String email;

    @NotBlank
    @Size(min = 5, max = 128)
    @Pattern(regexp = "([+]*[0-9]{1,4}\\s?[(]*\\d[0-9]{2,4}[)]*\\s?\\d{3}[-]*\\d{2}[-]*\\d{2})",
            message = "Please fill the phone number in format +1 (234) 567-89-10")
    private String phoneNumber;

    @NotBlank
    @Size(min = 5, max = 128)
    private String website;

    @Range(min = 1, max = 5,
            message = "Hotel class should be between 1 & 5 stars")
    private Integer hotelClass;

    private String description;

    //https://vladmihalcea.com/postgresql-array-java-list/
    @Type(type = "list-array")
    private List<String> roomFeatures;

    @Type(type = "list-array")
    private List<String> roomTypes;

    @Type(type = "list-array")
    private List<String> servicesAndFacilitates;

    @Type(type = "list-array")
    private List<String> languagesUsed;

    @Type(type = "list-array")
    private List<String> hotelStyle;

    @Type(type = "list-array")
    private List<String> fileNames;

    public String id() {
        Assert.notNull(id, "Entity must have id");
        return id;
    }

    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(ProxyUtils.getUserClass(o))) {
            return false;
        }
        Hotel that = (Hotel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}