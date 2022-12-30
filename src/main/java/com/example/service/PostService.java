package com.example.service;

import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.model.AlarmArgs;
import com.example.model.AlarmType;
import com.example.model.Comment;
import com.example.model.Post;
import com.example.model.entity.*;
import com.example.repository.*;
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
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;

    @Transactional
    public void create(String title, String body, String username){
        UserEntity userEntity = getUserEntityOrException(username);

        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String username, Integer postId){
        UserEntity userEntity = getUserEntityOrException(username);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

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
        UserEntity userEntity = getUserEntityOrException(username);

        PostEntity postEntity = getPostEntityOrException(postId);

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
        UserEntity userEntity = getUserEntityOrException(username);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String username){
        UserEntity userEntity = getUserEntityOrException(username);

        PostEntity postEntity = getPostEntityOrException(postId);

        // check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("%s already liked post %d", username, postId));
        });

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ONE_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
    }

    @Transactional
    public int likeCount(Integer postId){
        PostEntity postEntity = getPostEntityOrException(postId);

        // count like
        // AS-IS
        // List<LikeEntity> likeEntities = likeEntityRepository.findAllByPost(postEntity);
        // return likeEntities.size();

        // TO-BE
        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String username, String comment) {
        UserEntity userEntity = getUserEntityOrException(username);

        PostEntity postEntity = getPostEntityOrException(postId);

        // comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
    }


    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntityOrException(postId);

        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    // post exist
    private PostEntity getPostEntityOrException(Integer postId) {
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    // user exist
    private UserEntity getUserEntityOrException(String username) {
        return userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));
    }
}
