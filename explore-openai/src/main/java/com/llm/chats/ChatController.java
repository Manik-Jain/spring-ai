package com.llm.chats;

import com.llm.dto.UserInput;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/v1/chats")
    public String chat(@RequestBody UserInput userInput) {
        var response = chatClient.prompt(userInput.prompt()).call();
        return response.content();
    }

    @PostMapping("/v1/chats/stream")
    public Flux<String> chatStream(@RequestBody UserInput userInput) {
        var response = chatClient.prompt(userInput.prompt()).stream();
        return response.content();
    }
}
