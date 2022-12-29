package com.example.fixture;

import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String username, Integer postId, Integer userId){
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(username);

        PostEntity result = new PostEntity();
        result.setId(postId);
        result.setUser(user);

        return result;
    }
}
