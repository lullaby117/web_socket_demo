package com.jin.web_socket_demo.listener;


import com.jin.web_socket_demo.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * 用户断开连接时通知所有人
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setType("LEAVE");
            leaveMessage.setSender(username);
            leaveMessage.setContent(username + " 离开了聊天室");
            leaveMessage.setTimestamp(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            // 广播离开消息
            messagingTemplate.convertAndSend("/topic/public", leaveMessage);
        }
    }

    /**
     * 用户连接成功时记录日志（可选）
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("【WebSocket】新用户连接");
    }
}