package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    private String title;
    private String body;

    public static PostCreateRequest of(String title, String body) {
        return new PostCreateRequest(title, body);
    }
}
