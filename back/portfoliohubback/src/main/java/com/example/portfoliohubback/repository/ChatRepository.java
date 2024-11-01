package com.example.portfoliohubback.repository;

import com.example.portfoliohubback.entity.ChatMessageEntity;
import com.example.portfoliohubback.entity.ChatRoomEntity;
import com.example.portfoliohubback.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatRoomEntity, Long> {
    List<ChatRoomEntity> findByChatSender(UserEntity sender);
    List<ChatRoomEntity> findByChatReceiver(UserEntity receiver);

    @Query("SELECT cr FROM ChatRoomEntity cr WHERE cr.chatSender.id = :senderId AND cr.chatReceiver.id = :receiverId")
    List<ChatRoomEntity> findBySenderAndReceiver(@Param("senderId") String senderId, @Param("receiverId") String receiverId);

    Optional<ChatRoomEntity> findByChatSenderAndChatReceiver(UserEntity sender, UserEntity receiver);


    @Query("SELECT cm FROM ChatMessageEntity cm WHERE cm.chatSender = :sender AND cm.chatReceiver = :receiver ORDER BY cm.messageTimestamp ASC")
    List<ChatMessageEntity> findMessagesByChatSenderAndChatReceiver(@Param("sender") UserEntity sender, @Param("receiver") UserEntity receiver);

}
