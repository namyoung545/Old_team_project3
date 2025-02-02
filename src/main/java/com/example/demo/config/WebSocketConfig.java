package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket  // WebSocket 기능 활성화
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // TestWebsocketHandler 매핑
        registry.addHandler(testWebSocketHandler(), "/testSock/")
                .addInterceptors(new HttpSessionHandshakeInterceptor())  // Handshake 인터셉터 추가
                .withSockJS();  // SockJS를 사용하여 웹소켓 연결

        // ChattingWebsocketHandler 매핑
        registry.addHandler(chatWebSocketHandler(), "/chattingSock/")
                .addInterceptors(new HttpSessionHandshakeInterceptor())  // Handshake 인터셉터 추가
                .withSockJS();  // SockJS 사용
    }

    @Bean
    public WebSocketHandler testWebSocketHandler() {
        return new TestWebsocketHandler();  // TestWebsocketHandler 빈 등록
    }

    @Bean
    public WebSocketHandler chatWebSocketHandler() {
        return new ChattingWebSocketHandler();  // ChattingWebsocketHandler 빈 등록
    }
}
