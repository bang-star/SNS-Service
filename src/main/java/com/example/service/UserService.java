package com.example.service;

import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.model.User;
import com.example.model.entity.UserEntity;
import com.example.repository.UserEntityRepository;
import com.example.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    public String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    public Long expiredTimeMs;


    @Transactional
    public User join(String username, String password){
        // 회원가입하려는 username으로 회원가입된 user가 있는지
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", username));
        });

        // 회원가입 진행 = user 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(username, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    public String login(String username, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        // 비밀번호 체크
        if(!encoder.matches(password, userEntity.getPassword())){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(username, secretKey, expiredTimeMs);

        return token;
    }
}
