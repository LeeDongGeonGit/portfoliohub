package com.example.portfoliohubback.controller;


import com.example.portfoliohubback.controller.request.ChatMessageRequest;
import com.example.portfoliohubback.controller.response.ApiResponse;
import com.example.portfoliohubback.controller.response.ChatMessageResponse;
import com.example.portfoliohubback.controller.response.ChatRoomResponse;
import com.example.portfoliohubback.controller.response.ResponseCode;
import com.example.portfoliohubback.entity.ChatMessageEntity;
import com.example.portfoliohubback.entity.ChatRoomEntity;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.repository.ChatMessageRepository;
import com.example.portfoliohubback.repository.ChatRepository;
import com.example.portfoliohubback.repository.BanRepository;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BanRepository banRepository;
    private String getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }


    //송신자 아이디 받아오기
    @GetMapping("/getSenderInfo")
    public ApiResponse<String> getSenderInfo(
            @RequestHeader("Authorization") String token) {

        String userId = getUserIdFromToken();
        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        return ApiResponse.response(true, ResponseCode.SUCCESS, userId);
    }

    //채팅방리스트 불러오기
    @GetMapping("/chatRoomList")
    public ApiResponse<List<ChatRoomResponse.ChatRoomInfo>> getChatRooms(@RequestHeader("Authorization") String userId) {

        // 사용자 아이디 확인
        userId = getUserIdFromToken();

        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        // 사용자 정보 가져오기
        UserEntity sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 없습니다."));

        // 해당 사용자가 송신자인 채팅방 목록 가져오기
        List<ChatRoomEntity> chatRooms = chatRepository.findByChatSender(sender);

        // 채팅방 정보와 마지막 메시지 정보를 담을 리스트 생성
        List<ChatRoomResponse.ChatRoomInfo> chatRoomInfos = new ArrayList<>();

        // 각 채팅방에 대한 정보 조회
        for (ChatRoomEntity chatRoom : chatRooms) {
            // 수신자 정보 가져오기
            UserEntity receiver = chatRoom.getChatReceiver();

            // 두 방향의 메시지를 모두 확인
            ChatMessageEntity lastMessage = getLastMessage(sender, receiver);
            LocalDateTime lastMessageTimestamp = lastMessage.getMessageTimestamp();

            // ChatRoomResponse.ChatRoomInfo 클래스의 생성자를 사용하여 chatRoomInfo 객체 생성
            ChatRoomResponse.ChatRoomInfo chatRoomInfo = ChatRoomResponse.ChatRoomInfo.of(chatRoom, lastMessage.getMessageContent(), lastMessageTimestamp);

            // 리스트에 추가
            chatRoomInfos.add(chatRoomInfo);
        }

        return ApiResponse.response(true, ResponseCode.SUCCESS, chatRoomInfos);
    }

    private ChatMessageEntity getLastMessage(UserEntity sender, UserEntity receiver) {
        // 송신자 -> 수신자 메시지 목록 가져오기
        List<ChatMessageEntity> sentMessages = chatRepository.findMessagesByChatSenderAndChatReceiver(sender, receiver);
        // 수신자 -> 송신자 메시지 목록 가져오기
        List<ChatMessageEntity> receivedMessages = chatRepository.findMessagesByChatSenderAndChatReceiver(receiver, sender);

        // 두 목록에서 가장 최신의 메시지 찾기
        ChatMessageEntity lastSentMessage = !sentMessages.isEmpty() ? sentMessages.get(sentMessages.size() - 1) : null;
        ChatMessageEntity lastReceivedMessage = !receivedMessages.isEmpty() ? receivedMessages.get(receivedMessages.size() - 1) : null;

        if (lastSentMessage == null && lastReceivedMessage == null) {
            ChatMessageEntity noMessage = new ChatMessageEntity();
            noMessage.setMessageContent("대화 내용이 없음");
            noMessage.setMessageTimestamp(null);
            return noMessage;
        } else if (lastSentMessage == null) {
            return lastReceivedMessage;
        } else if (lastReceivedMessage == null) {
            return lastSentMessage;
        } else {
            return lastSentMessage.getMessageTimestamp().isAfter(lastReceivedMessage.getMessageTimestamp()) ? lastSentMessage : lastReceivedMessage;
        }
    }

    private void processChatRoom(ChatRoomEntity chatRoom, List<ChatRoomResponse.ChatRoomInfo> chatRoomInfos) {
        // 채팅방의 마지막 메시지 가져오기
        List<ChatMessageEntity> messages = chatRoom.getMessages();
        ChatMessageEntity lastMessage = null;
        LocalDateTime lastMessageTimestamp = null;

        if (!messages.isEmpty()) {
            lastMessage = messages.get(messages.size() - 1);
            lastMessageTimestamp = lastMessage.getMessageTimestamp();
        } else {
            lastMessage = new ChatMessageEntity();
            lastMessage.setMessageContent("대화 내용이 없음");
            lastMessageTimestamp = null; // 대화내용이 없으면 시간은 없음
        }

        // ChatRoomResponse.ChatRoomInfo 클래스의 생성자를 사용하여 chatRoomInfo 객체 생성
        ChatRoomResponse.ChatRoomInfo chatRoomInfo = ChatRoomResponse.ChatRoomInfo.of(chatRoom, lastMessage.getMessageContent(), lastMessageTimestamp);

        // 리스트에 추가
        chatRoomInfos.add(chatRoomInfo);
    }

    // 채팅방 상대방이 차단된 상대인지 확인
    @GetMapping("/chatRoomBanTest/{ChatUserId}")
    public ApiResponse<Boolean> getBanTest(
            @RequestHeader("Authorization") String token,
            @PathVariable String ChatUserId) {

        String userId = getUserIdFromToken();
        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        UserEntity fromUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("송신자를 찾을 수 없습니다."));
        UserEntity toUser = userRepository.findById(ChatUserId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        boolean isBanned = banRepository.existsByFromUserAndToUser(fromUser, toUser);

        return ApiResponse.response(true, ResponseCode.SUCCESS, !isBanned);
    }



    //채팅 메시지 내역 불러오기
    @GetMapping("/messages/{receiverId}")
    public ApiResponse<List<ChatMessageResponse.chatMessageOne>> getMessagesByReceiverId(
            @RequestHeader("Authorization") String userId,
            @PathVariable String receiverId) {
        // 사용자 아이디 확인 및 권한 검사
        userId = getUserIdFromToken();
        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        // 송신자가 userId이고 수신자가 receiverId인 채팅방 목록 가져오기
        List<ChatRoomEntity> chatRooms1 = chatRepository.findBySenderAndReceiver(userId, receiverId);

        // 송신자가 receiverId이고 수신자가 userId인 채팅방 목록 가져오기
        List<ChatRoomEntity> chatRooms2 = chatRepository.findBySenderAndReceiver(receiverId, userId);

        // 위 두 목록을 합쳐서 하나의 목록으로 만듦
        List<ChatRoomEntity> allChatRooms = new ArrayList<>();
        allChatRooms.addAll(chatRooms1);
        allChatRooms.addAll(chatRooms2);

        // 모든 채팅방에 있는 메시지들을 가져와 하나의 리스트로 만듦
        List<ChatMessageEntity> messages = new ArrayList<>();
        for (ChatRoomEntity chatRoom : allChatRooms) {
            messages.addAll(chatRoom.getMessages());
        }

        // 시간 순서대로 메시지를 정렬
        Collections.sort(messages, Comparator.comparing(ChatMessageEntity::getMessageTimestamp));

        // 메시지를 응답 형식으로 변환
        List<ChatMessageResponse.chatMessageOne> messageResponses = messages.stream()
                .map(ChatMessageResponse.chatMessageOne::of)
                .collect(Collectors.toList());

        // 응답 반환
        return ApiResponse.response(true, ResponseCode.SUCCESS, messageResponses);
    }


    //채팅 메시지 추가하기
    @PostMapping("/messageCreate")
    public ApiResponse<ChatMessageResponse.chatMessageOne> createChatMessage(
            @RequestBody ChatMessageRequest.Create chatMessage,
            @RequestHeader("Authorization") String userId) {

        // 사용자 아이디 확인
        userId = getUserIdFromToken();

        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        // 송신자 ID와 수신자 ID를 이용하여 채팅방을 찾음
        UserEntity sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 송신자가 없습니다."));
        UserEntity receiver = userRepository.findById(chatMessage.getChatReceiver())
                .orElseThrow(() -> new RuntimeException("해당 수신자가 없습니다."));

        // 채팅방을 찾는 방법을 수정하여 송신자와 수신자로 채팅방을 찾음
        ChatRoomEntity chatRoom = chatRepository.findByChatSenderAndChatReceiver(sender, receiver)
                .orElseThrow(() -> new RuntimeException("해당 채팅방이 없습니다."));

        // 새로운 채팅 메시지 생성
        ChatMessageEntity newChatMessage = new ChatMessageEntity();
        newChatMessage.setChatRoom(chatRoom);
        newChatMessage.setChatSender(sender);
        newChatMessage.setChatReceiver(receiver);
        newChatMessage.setMessageContent(chatMessage.getMessageContent());
        newChatMessage.setMessageRead(false);
        newChatMessage.setMessageTimestamp(chatMessage.getMessageTimestamp()); // 현재 시간 설정

        // DB에 메시지 저장
        ChatMessageEntity savedMessage = chatMessageRepository.save(newChatMessage);
        ChatMessageResponse.chatMessageOne responseMessage = ChatMessageResponse.chatMessageOne.of(savedMessage);

        return ApiResponse.response(true, ResponseCode.Created, responseMessage);
    }

    //새로운 채팅방 추가하기
    @PostMapping("/roomCreate/{receiverId}")
    public ApiResponse<ChatRoomResponse.chatRoomOne> createChatRoom(@PathVariable String receiverId,
                                                                    @RequestHeader("Authorization") String userId) {

        // 송신자 아이디 가져오기
        userId = getUserIdFromToken();

        // 송신자 로그인 여부 확인
        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.BAD_REQUEST, null);
        }

        // 송신자 확인
        UserEntity sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 송신자가 없습니다."));

        // 수신자 확인
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("해당 수신자가 없습니다."));

        // 송신자와 수신자로 이미 존재하는 채팅방 확인
        Optional<ChatRoomEntity> existingChatRoom = chatRepository.findByChatSenderAndChatReceiver(sender, receiver);

        // 반대로 존재하는 채팅방 확인
        Optional<ChatRoomEntity> reverseExistingChatRoom = chatRepository.findByChatSenderAndChatReceiver(receiver, sender);

        // 이미 존재하는 채팅방이 없으면 새로운 채팅방 생성
        if (existingChatRoom.isEmpty() && reverseExistingChatRoom.isEmpty()) {
            // 새로운 채팅방 생성 (송신자 -> 수신자)
            ChatRoomEntity newChatRoom = new ChatRoomEntity();
            newChatRoom.setChatSender(sender);
            newChatRoom.setChatReceiver(receiver);

            // 새로운 채팅방 생성 (수신자 -> 송신자)
            ChatRoomEntity newChatRoomReverse = new ChatRoomEntity();
            newChatRoomReverse.setChatSender(receiver);
            newChatRoomReverse.setChatReceiver(sender);

            // 채팅방 저장
            chatRepository.save(newChatRoom);
            ChatRoomEntity savedChatRoomReverse = chatRepository.save(newChatRoomReverse);

            // 응답으로 새로운 채팅방 정보 반환 (임의로 한 채팅방의 정보를 반환)
            ChatRoomResponse.chatRoomOne chatRoomInfo = ChatRoomResponse.chatRoomOne.of(savedChatRoomReverse);
            return ApiResponse.response(true, ResponseCode.Created, chatRoomInfo);
        } else {
            // 이미 존재하는 채팅방이 있으면 해당 정보 반환
            ChatRoomEntity existingRoom = existingChatRoom.orElse(reverseExistingChatRoom.orElse(null));
            ChatRoomResponse.chatRoomOne chatRoomInfo = ChatRoomResponse.chatRoomOne.of(existingRoom);
            return ApiResponse.response(true, ResponseCode.OK, chatRoomInfo);
        }
    }
}
