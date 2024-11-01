package com.example.portfoliohubback.repository;

import com.example.portfoliohubback.entity.BanEntity;
import com.example.portfoliohubback.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanRepository extends JpaRepository<BanEntity, Long> {
    Page<BanEntity> findByFromUser(UserEntity userEntity, Pageable pageable);
    List<BanEntity> findByFromUserOrToUser(UserEntity fromUser, UserEntity toUser);
    boolean existsByFromUserAndToUser(UserEntity fromUser, UserEntity toUser);
}
