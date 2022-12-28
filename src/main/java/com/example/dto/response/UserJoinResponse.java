package com.example.dto.response;

import com.example.model.User;
import com.example.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponse {

    private Integer id;
    private String username;
    private UserRole role;

    public static UserJoinResponse fromUser(User user){
        return new UserJoinResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
