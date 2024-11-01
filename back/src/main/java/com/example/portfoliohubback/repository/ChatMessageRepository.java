package com.example.portfoliohubback.repository;

import com.example.portfoliohubback.entity.ChatMessageEntity;
import com.example.portfoliohubback.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByChatSenderAndChatReceiver(UserEntity chatSender, UserEntity chatReceiver);
}
