package com.example.service;

import com.example.model.Post;
import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;
import com.example.repository.PostEntityRepository;
import com.example.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public Post modify(String title, String body, String username, Integer postId){
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String username, Integer postId){
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        postEntityRepository.delete(postEntity);
    }

    @Transactional(readOnly = true)
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<Post> mypost(String username, Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }
}
