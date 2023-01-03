package com.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinRequest {

    private String name;
    private String password;

    public static UserJoinRequest of(String name, String password) {
        return new UserJoinRequest(name, password);
    }
}
