package com.example.clients.review;

import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class ReviewResponse {
    String id;
    Long count;
}