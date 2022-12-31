package com.example.repository;

import com.example.model.entity.AlarmEntity;
import com.example.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmEntityRepository extends JpaRepository<AlarmEntity, Integer> {

    // AS-IS
    Page<AlarmEntity> findAllByUser(UserEntity user, Pageable pageable);

    // TO-BE
    Page<AlarmEntity> findAllByUserId(Integer userId, Pageable pageable);
}
