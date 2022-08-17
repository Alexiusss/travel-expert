package com.example.review.model;

import com.example.common.HasId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.Instant;

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

    @Version
    protected int version;

    private String title;

    private String description;

    private Integer rating;

    private String filename;

    private String userId;

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