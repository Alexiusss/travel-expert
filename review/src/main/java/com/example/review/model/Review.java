package com.example.review.model;

import com.example.common.HasId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

@TypeDefs(
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        )
)
@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "reviews")
public class Review implements HasId {

    @Id
    @GeneratedValue(generator = "custom-generator",
            strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-generator",
            strategy = "com.example.review.model.id.generator.BaseIdentifierGenerator")
    private String id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant modifiedAt;

    @Column(name = "active", nullable = false, columnDefinition = "bool default false")
    private boolean active;

    @Version
    protected int version;

    @NotBlank
    @Size(min = 5, max = 128)
    private String title;

    @Column(name = "description", length = 2048)
    @NotBlank(message = "Please fill the description")
    @Length(max = 2048, message = "Description too long(more than to 2k)")
    private String description;

    @Range(min = 1, max = 5)
    private Integer rating;

    @Type(type = "string-array")
    @Column(
            name = "file_names",
            columnDefinition = "text[]"
    )
    private String[] fileNames;

    @NotBlank
    private String userId;

    @NotBlank
    private String itemId;

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
        Review that = (Review) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}