package com.example.portfoliohubback.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_bulletin_board")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamBulletinBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name")
    private String projectName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "project_startdate")
    private LocalDateTime projectStartDate;

    @Column(name = "project_enddate")
    private LocalDateTime projectEndDate;

    @Column(name = "project_local",length = 40)
    private String projectLocal;

    @Column(name = "project_member_count")
    private int projectMemberCount;

    @Column(name = "project_development_field", length = 30)
    private String projectDevelopmentField;

    @Column(name = "project_description",columnDefinition = "TEXT")
    private String projectDescription;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
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
