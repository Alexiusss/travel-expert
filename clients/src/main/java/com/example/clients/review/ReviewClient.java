package com.example.clients.review;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "review", url = "${clients.review.url}")
public interface ReviewClient {
    @PostMapping("api/v1/reviews/user/reviewCountActive")
    List<ReviewResponse> getActiveList (@RequestBody String[] authors);
}