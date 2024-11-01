package com.example.portfoliohubback.repository;


import com.example.portfoliohubback.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
    Page<UserEntity> findByNameContaining(String name, Pageable pageable);
    Page<UserEntity> findByNameContainingAndIdNotIn(String name, Collection<String> id, Pageable pageable);
}
