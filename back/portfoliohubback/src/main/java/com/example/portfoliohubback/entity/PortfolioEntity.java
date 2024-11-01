package com.example.portfoliohubback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "position")
    private String position;

    @Column(name = "career")
    private String career;

    @Column(name = "count")
    private Long count;
    @Column(length = 1000)
    private String profile;

    @ManyToOne
    private UserEntity user;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 내용을 저장할 필드

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
