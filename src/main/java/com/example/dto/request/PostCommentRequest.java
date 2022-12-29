package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequest {
    private String comment;

    public static PostCommentRequest of(String comment) {
        return new PostCommentRequest(comment);
    }
}
