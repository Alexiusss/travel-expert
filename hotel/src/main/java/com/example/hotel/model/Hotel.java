package com.example.hotel.model;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
@Table(name = "hotels")
public class Hotel {

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

    private String name;

    private String address;

    private String email;

    private String phoneNumber;

    private String website;

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
}