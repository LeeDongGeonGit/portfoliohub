package com.example.portfoliohubback.entity;


import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "follower")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FollowerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private UserEntity followee;
    @Column(name = "look_followee")
    private boolean lookFollowee;

    // 생성될때, 자동으로 현재 시간을 넣어줌
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 업데이트 시간을 자동으로 넣어줌
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}