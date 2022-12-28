package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinRequest {

    private String username;
    private String password;

    public static UserJoinRequest of(String username, String password) {
        return new UserJoinRequest(username, password);
    }
}
