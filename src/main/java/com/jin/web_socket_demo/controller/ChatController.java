package com.jin.web_socket_demo.controller;


import com.jin.web_socket_demo.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    // 注入消息发送模板（用于点对点消息）
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 群聊消息处理
     * 客户端发送地址：/app/chat.sendMessage
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // 添加时间戳
        chatMessage.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return chatMessage;
    }

    /**
     * 用户加入通知
     * 客户端发送地址：/app/chat.addUser
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // 将用户名存入WebSocket会话中（用于后续识别）
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setType("JOIN");
        chatMessage.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return chatMessage;
    }

    /**
     * 私聊消息处理
     * 客户端发送地址：/app/chat.privateMessage
     */
    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        // 发送给指定用户
        messagingTemplate.convertAndSendToUser(
                chatMessage.getTargetUser(),  // 目标用户名
                "/queue/private",              // 私聊队列
                chatMessage
        );
    }
}