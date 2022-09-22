package com.example.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Rating {
    Double averageRating;
    Map<Integer, Integer> ratingsMap;
}