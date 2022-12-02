package com.example.review.model.dto;

import com.example.clients.auth.AuthorDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;

@Getter
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private String id;
    private Instant createdAt;
    private String title;
    private String description;
    private String[] fileNames;
    private boolean active;
    private Integer rating;
    private Set<String> likes;
    private AuthorDTO author;
}