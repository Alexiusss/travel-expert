package com.example.notification.model;

import com.example.common.HasId;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "notifications")
public class Notification implements HasId {

    @Id
    @GeneratedValue(generator = "custom-generator",
            strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-generator",
            strategy = "com.example.notification.model.id.generator.BaseIdentifierGenerator")
    private String id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant sentAt;
    private String toRecipientId;
    private String roRecipientEmail;
    private String sender;
    private String message;
}