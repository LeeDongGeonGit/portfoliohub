package com.example.portfoliohubback.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEntity {

    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoomEntity chatRoom;

    @ManyToOne
    @JoinColumn(name = "chat_sender_id")
    private UserEntity chatSender;

    @ManyToOne
    @JoinColumn(name = "chat_receiver_id")
    private UserEntity chatReceiver;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "message_timestamp")
    private LocalDateTime messageTimestamp;

    @Column(name = "message_read")
    private boolean messageRead;

    // 생성될때, 자동으로 현재 시간을 넣어줌
    @PrePersist
    public void prePersist() {
        this.messageTimestamp = LocalDateTime.now();

    }
}