package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginRequest {

    private String username;
    private String password;

    public static UserLoginRequest of(String username, String password) {
        return new UserLoginRequest(username, password);
    }
}
