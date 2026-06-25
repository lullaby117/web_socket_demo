package com.jin.web_socket_demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // 启用WebSocket消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 消息代理前缀（客户端订阅消息的地址）
        config.enableSimpleBroker("/topic", "/queue");
        // 消息映射前缀（客户端发送消息的地址）
        config.setApplicationDestinationPrefixes("/app");
        // 点对点消息前缀（用于私聊）
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，客户端通过此地址连接
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("*")  // 允许跨域
                .withSockJS();            // 启用SockJS降级方案
    }
}