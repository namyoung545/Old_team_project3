package com.example.demo.PHG_DeepSeek;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private OllamaService ollamaService;

    @PostMapping("/send")
    public String sendMessage(@RequestBody ChatRequestDTO request) {
        return ollamaService.generateResponse(
                request.getMessage(),
                request.getModel(),
                request.getSessionId());
    }
}