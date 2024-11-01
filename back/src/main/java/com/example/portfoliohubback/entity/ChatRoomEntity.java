package com.example.portfoliohubback.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatRoomEntity {

    @Id
    @Column(name = "chat_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @ManyToOne
    @JoinColumn(name = "chat_sender_id")
    private UserEntity chatSender;

    @ManyToOne
    @JoinColumn(name = "chat_receiver_id")
    private UserEntity chatReceiver;

    @OneToOne(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("chatRoom")
    private VideoCallRoomEntity videoCallRoom;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("chatRoom")
    private List<ChatMessageEntity> messages;
}