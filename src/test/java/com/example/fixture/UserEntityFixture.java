package com.example.fixture;

import com.example.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String username, String password){
        UserEntity result = new UserEntity();
        result.setId(1);
        result.setUsername(username);
        result.setPassword(password);

        return result;
    }
}
