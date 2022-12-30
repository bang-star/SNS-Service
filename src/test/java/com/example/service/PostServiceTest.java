package com.example.service;

import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.fixture.PostEntityFixture;
import com.example.fixture.UserEntityFixture;
import com.example.model.AlarmArgs;
import com.example.model.AlarmType;
import com.example.model.entity.AlarmEntity;
import com.example.model.entity.LikeEntity;
import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;
import com.example.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class PostServiceTest {

    @Autowired private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean private UserEntityRepository userEntityRepository;
    @MockBean private LikeEntityRepository likeEntityRepository;
    @MockBean private CommentEntityRepository commentEntityRepository;
    @MockBean private AlarmEntityRepository alarmEntityRepository;

    @Test
    void 포스트작성이_성공한경우() {
        String title = "title";
        String body = "body";
        String username = "username";

        // mocking
        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title, body, username));
    }

    @Test
    void 포스트작성시_요청한유저가_존재하지않은경우(){
        String title = "title";
        String body = "body";
        String username = "username";

        // mocking
        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.create(title, body, username));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정이_성공한경우() {
        String title = "title";
        String body = "body";
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity user = postEntity.getUser();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(postEntity)).thenReturn(postEntity);

        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, username, postId));
    }

    @Test
    void 포스트수정시_포스트가_존재하지않는_경우() {
        String title = "title";
        String body = "body";
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity user = postEntity.getUser();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, username, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정시_권한이_없는_경우() {
        String title = "title";
        String body = "body";
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity writer = UserEntityFixture.get("username1", "password1", 2);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, username, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 포스트삭제가_성공한경우() {
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity user = postEntity.getUser();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.delete(username, postId));
    }

    @Test
    void 포스트삭제시_포스트가_존재하지않는_경우() {
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity user = postEntity.getUser();

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(username, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트삭제시_권한이_없는_경우() {
        String username = "username";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(username, postId, 1);
        UserEntity writer = UserEntityFixture.get("username1", "password1", 2);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(username, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 피드목록요청이_성공한경우() {
        Pageable pageable = mock(Pageable.class);

        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 내피드목록요청이_성공한경우() {
        UserEntity user = mock(UserEntity.class);
        Pageable pageable = mock(Pageable.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.mypost("", pageable));
    }

    @Test
    void 좋아요요청이_성공한경우() {
        Integer postId = 1;
        String username = "username";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeEntityRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> postService.like(postId, username));
    }

    @Test
    void 좋아요요청시_포스트가_존재하지않는_경우() {
        Integer postId = 1;
        String username = "username";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());
        when(likeEntityRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.like(postId, username));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 좋아요요청시_이미좋아요한_게시물인경우() {
        Integer postId = 1;
        String username = "username";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);
        LikeEntity like = mock(LikeEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeEntityRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.like(postId, username));
        Assertions.assertEquals(ErrorCode.ALREADY_LIKED, e.getErrorCode());
    }

    @Test
    void 좋아요개수요청이_성공한경우() {
        Integer postId = 1;
        String username = "username";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeEntityRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> postService.like(postId, username));
    }

    @Test
    void 댓글생성이_성공한경우(){
        Integer postId = 1;
        String comment = "comment";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> postService.comment(postId, "username", comment));
    }

    @Test
    void 댓글생성시_포스트가_존재하지않는_경우() {
        Integer postId = 1;
        String comment = "comment";
        UserEntity user = mock(UserEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.comment(postId, "username", comment));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 댓글생성시_요청한유저가_존재하지않은경우() {
        Integer postId = 1;
        String comment = "comment";
        PostEntity post = mock(PostEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));

        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.comment(postId, "username", comment));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 댓글리스트요청시_성공한경우() {
        Integer postId = 1;
        PostEntity post = mock(PostEntity.class);
        Pageable pageable = mock(Pageable.class);

        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentEntityRepository.findAllByPost(post, pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.getComments(postId, pageable));
    }

    @Test
    void 알림_댓글생성시_알림메시지전달이_성공한경우(){
        Integer postId = 1;
        String comment = "comment";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);
        AlarmEntity alarm = mock(AlarmEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(alarmEntityRepository.save(AlarmEntity.of(post.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(user.getId(), post.getId()))))
                .thenReturn(alarm);

        Assertions.assertDoesNotThrow(() -> postService.comment(postId, "username", comment));
    }

    @Test
    void 알림_좋아요버튼클릭시_알림메시지전달이_성공한경우(){
        Integer postId = 1;
        String comment = "comment";
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);
        AlarmEntity alarm = mock(AlarmEntity.class);

        when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(alarmEntityRepository.save(AlarmEntity.of(post.getUser(), AlarmType.NEW_LIKE_ONE_POST, new AlarmArgs(user.getId(), post.getId())))).thenReturn(alarm);

        Assertions.assertDoesNotThrow(() -> postService.comment(postId, "username", comment));
    }

}
