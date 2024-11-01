package com.example.portfoliohubback.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "ban")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private Long banId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name = "id3")
    private UserEntity toUser;

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