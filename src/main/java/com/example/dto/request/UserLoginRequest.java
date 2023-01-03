package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginRequest {

    private String name;
    private String password;

    public static UserLoginRequest of(String name, String password) {
        return new UserLoginRequest(name, password);
    }
}
