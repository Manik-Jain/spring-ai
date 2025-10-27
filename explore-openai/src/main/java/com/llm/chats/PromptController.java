package com.llm.chats;

import com.llm.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PromptController {

    private static final Logger log = LoggerFactory.getLogger(PromptController.class);
    private final ChatClient chatClient;

    @Value("classpath:/prompt-templates/coding-assistant.st")
    private Resource systemText;


    public PromptController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    String systemRole = "You are a helpful assistant, who can answer java based questions.For any other questions, please respond with I don't know in a funny way!";

    @PostMapping("/v1/prompts")
    public String prompts(@RequestBody UserInput userInput) {
        var responseSpec = chatClient
                .prompt()
                .system(systemRole)
                .user(userInput.prompt())
                .call();

        log.info("responseSpec : {} ", responseSpec);
        return responseSpec.content();
    }

    //preferred style of invocation
    @PostMapping("/v1/prompt/{language}")
    public String prompt(@RequestBody UserInput userInput, @PathVariable("language") String language) throws Exception {
        var systemPromptTemplate = new SystemPromptTemplate(systemText);
        var systemMessage = systemPromptTemplate.createMessage(Map.of("language", language));
        log.info("Loaded System Message from Template: {}", systemMessage);

        //var systemMessage = new SystemMessage(systemRole);
        var prompt = new Prompt(List.of(
                systemMessage,
                new UserMessage(userInput.prompt())
                //new AssistantMessage("Sure! I can help you with Java programming questions.")
        ));

        var responseSpec = chatClient
                .prompt(prompt)
                .call();

        log.info("responseSpec : {} ", responseSpec);
        return responseSpec.content();
    }


}
