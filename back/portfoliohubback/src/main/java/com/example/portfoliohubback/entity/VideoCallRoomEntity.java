package com.example.portfoliohubback.entity;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "video_call_room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VideoCallRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vcroom_id")
    private Long vcroomId;

    @OneToOne
    @JoinColumn(name = "chat_room_id")
    @JsonIgnoreProperties("videoCallRoom")
    private ChatRoomEntity chatRoom;

    @Column(name = "vcroom_name")
    private String vcroomName;

    @Column(name = "vcroom_capacity")
    private Integer vcroomCapacity;

    @Column(name = "vcroom_created_at")
    private LocalDateTime vcroomCreatedAt;

    @Column(name = "vcroom_enter_user")
    private Integer vcroomEnterUser;
    // 생성될때, 자동으로 현재 시간을 넣어줌
    @PrePersist
    public void prePersist() {
        this.vcroomCreatedAt = LocalDateTime.now();
    }

    public VideoCallRoomEntity create(String vcroomName, Integer vcroomCapacity, LocalDateTime vcroomCreatedAt) {
        return VideoCallRoomEntity.builder()
                .vcroomName(vcroomName)
                .vcroomCapacity(vcroomCapacity)
                .vcroomCreatedAt(vcroomCreatedAt != null ? vcroomCreatedAt : LocalDateTime.now())
                .build();
    }
}