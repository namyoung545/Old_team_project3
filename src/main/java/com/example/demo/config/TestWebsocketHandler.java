package com.example.demo.config;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import java.io.IOException;  // 변경된 부분

public class TestWebsocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Test WebSocket 연결 성공");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {  // WebSocketException -> IOException
        // 메시지 처리 로직
        try {
            if (message instanceof TextMessage) {
                // TextMessage 처리
                TextMessage textMessage = (TextMessage) message;
                session.sendMessage(new TextMessage("받은 메시지: " + textMessage.getPayload()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException {  // WebSocketException -> IOException
        System.out.println("웹소켓 오류 발생: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("웹소켓 연결 종료");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;  // 전체 메시지만 지원
    }
}
