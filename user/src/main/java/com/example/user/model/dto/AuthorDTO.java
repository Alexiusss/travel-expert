package com.example.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorDTO {
    String authorId;
    String authorName;
    String fileName;
}