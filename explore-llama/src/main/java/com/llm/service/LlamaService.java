package com.llm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.llm.dto.LlamaResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LlamaService {

    private final ChatClient chatClient;
    ObjectMapper mapper = new ObjectMapper();

    public LlamaService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    public Flux<String> getChatStream(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .stream()
                .content()
                .log();
//                .map(chunk -> {
//                    LlamaResponse response = null;
//                    try {
//                        response = mapper.readValue(chunk, LlamaResponse.class);
//                        return response.getResponse();
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
    }
}
