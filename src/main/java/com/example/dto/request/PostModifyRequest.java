package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostModifyRequest {

    private String title;
    private String body;

    public static PostModifyRequest of(String title, String body) {
        return new PostModifyRequest(title, body);
    }
}
