package com.jin.web_socket_demo.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;      // 发送者
    private String content;     // 消息内容
    private String type;        // 消息类型：CHAT, JOIN, LEAVE
    private String timestamp;   // 时间戳
    private String targetUser;  // 私聊目标用户（可选）
}