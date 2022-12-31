package com.example.controller;

import com.example.dto.request.PostCommentRequest;
import com.example.dto.request.PostCreateRequest;
import com.example.dto.request.PostModifyRequest;
import com.example.dto.response.CommentResponse;
import com.example.dto.response.PostResponse;
import com.example.dto.response.Response;
import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.model.Post;
import com.example.model.User;
import com.example.service.PostService;
import com.example.utils.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        User user = getCastingToUserClassFailed(authentication);

        postService.create(request.getTitle(), request.getBody(), user);

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        User user = getCastingToUserClassFailed(authentication);

        Post post = postService.modify(request.getTitle(), request.getBody(), user, postId);

        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        User user = getCastingToUserClassFailed(authentication);

        postService.delete(user, postId);

        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {

        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> myPost(Pageable pageable, Authentication authentication) {
        User user = getCastingToUserClassFailed(authentication);

        return Response.success(postService.mypost(user, pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId, Authentication authentication) {
        User user = getCastingToUserClassFailed(authentication);

        postService.like(postId, user);

        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId, Authentication authentication) {
        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest request, Authentication authentication) {
        User user = getCastingToUserClassFailed(authentication);

        postService.comment(postId, user, request.getComment());

        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> comment(@PathVariable Integer postId, Pageable pageable, Authentication authentication) {
        User user = getCastingToUserClassFailed(authentication);

        return Response.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }

    private static User getCastingToUserClassFailed(Authentication authentication) {
        return ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed"));
    }
}
