package com.llm.service;


import com.llm.dtos.GroundingRequest;
import com.llm.dtos.GroundingResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroundingService {

    private static final Logger log = LoggerFactory.getLogger(GroundingService.class);

    private final ChatClient chatClient;
    private final PgVectorStore vectorStore;

    @Value("classpath:/prompt-templates/RAG-Prompt.st")
    private Resource ragPrompt;


    @Value("classpath:/prompt-templates/RAG-QA-Prompt.st")
    private Resource ragQAPrompt;

    public GroundingService(ChatClient.Builder chatClientBuilder, @Qualifier("qaVectorStore") PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    private Prompt getPrompt(Resource ragQAPrompt, String promptInput, String context){
        PromptTemplate promptTemplate = new PromptTemplate(ragQAPrompt);
        log.info("Loaded RAG QA Prompt Template: {}", promptTemplate.getTemplate());
        var promptMessage = promptTemplate.createMessage(Map.of("context", context,
                "input", promptInput));

        var prompt = new Prompt(List.of(promptMessage));
        return prompt;
    }

    public GroundingResponse grounding(GroundingRequest groundingRequest) {

        var searchRequest = SearchRequest.builder().query(groundingRequest.prompt()).build();
        List<Document> docs = this.vectorStore.doSimilaritySearch(searchRequest);

        log.info("Retrieved {} from vector store for the query.", docs);
        log.info("Retrieved {} documents from vector store for the query.", docs.size());

        //this context is fed to LLM for generating response
        var context = docs.stream().filter(Objects::nonNull).filter(document -> document.getScore()>= 0.8).limit(2)
                .map(doc -> doc.getText()).collect(Collectors.joining("\n---\n"));

        if (StringUtils.isNotEmpty(context)) {
            log.info("Using context: {}", context);

            //generate prompt template using ragQAPrompt
            Prompt prompt = getPrompt(ragQAPrompt, groundingRequest.prompt(), context);

            var callResponseSpec = this.chatClient.prompt(prompt).call();
            log.info("Generated response from LLM.");
            var response = callResponseSpec.content();
            log.info("LLM Response: {}", response);
            return new GroundingResponse(response);
        } else {
            log.info("No relevant context found for the query.");
            return new GroundingResponse("No relevant context found for the query.");
        }
    }

}
