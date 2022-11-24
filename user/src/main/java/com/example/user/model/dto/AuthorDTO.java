package com.example.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AuthorDTO {
    String authorId;
    String authorName;
    String username;
    String fileName;
    Instant registeredAt;
    Set<String> subscribers;
    Set<String> subscriptions;
    Long reviewsCount;
}