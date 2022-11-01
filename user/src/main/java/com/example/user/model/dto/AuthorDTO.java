package com.example.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class AuthorDTO {
    String authorId;
    String authorName;
    String username;
    String fileName;
    Instant registeredAt;
}