package com.example.portfoliohubback.repository;

import com.example.portfoliohubback.entity.FollowerEntity;
import com.example.portfoliohubback.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowerRepository extends JpaRepository<FollowerEntity, Long> {
    Page<FollowerEntity> findByFolloweeAndLookFollowee(UserEntity userEntity, boolean lookFollowee, Pageable pageable);
    Optional<FollowerEntity> findByUserAndFollowee(UserEntity userEntity, UserEntity user);
    Page<FollowerEntity> findByUser(UserEntity userEntity, Pageable pageable);
    Page<FollowerEntity> findByUserAndFolloweeNameContaining(UserEntity user, String name, Pageable pageable);
    Page<FollowerEntity> findByFolloweeAndLookFolloweeAndUserNameContaining(
            UserEntity userEntity, boolean lookFollowee, String name, Pageable pageable);

}
