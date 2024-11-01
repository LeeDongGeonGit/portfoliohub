package com.example.portfoliohubback.websocket;

import java.util.Date;

public class ChatMessage {
    private String senderId;  // 이 필드는 더 이상 필요하지 않지만, 클래스 구조는 그대로 유지합니다.
    private String chatReceiver;
    private String messageContent;
    private Date messageTimestamp;
    private boolean messageRead;

    // Default constructor
    public ChatMessage() {}

    // Parameterized constructor
    public ChatMessage(String senderId, String chatReceiver, String messageContent, Date messageTimestamp, boolean messageRead) {
        this.senderId = senderId;
        this.chatReceiver = chatReceiver;
        this.messageContent = messageContent;
        this.messageTimestamp = messageTimestamp;
        this.messageRead = messageRead;
    }

    // Getters and setters
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getChatReceiver() {
        return chatReceiver;
    }

    public void setChatReceiver(String chatReceiver) {
        this.chatReceiver = chatReceiver;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Date messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "senderId='" + senderId + '\'' +
                ", chatReceiver='" + chatReceiver + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", messageTimestamp=" + messageTimestamp +
                ", messageRead=" + messageRead +
                '}';
    }
}