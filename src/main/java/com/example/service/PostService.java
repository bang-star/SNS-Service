package com.example.service;

import com.example.SnsServiceApplication;
import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;
import com.example.repository.PostEntityRepository;
import com.example.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String username){
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

}
