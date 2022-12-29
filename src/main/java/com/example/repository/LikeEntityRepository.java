package com.example.repository;

import com.example.model.entity.LikeEntity;
import com.example.model.entity.PostEntity;
import com.example.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Long> {

    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    // AS-IS
    // List<LikeEntity> findAllByPost(PostEntity post);

    // TO-BE: PostgreSQL을 이용한 방법
    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post =:post")
    Integer countByPost(@Param("post") PostEntity post);
}
