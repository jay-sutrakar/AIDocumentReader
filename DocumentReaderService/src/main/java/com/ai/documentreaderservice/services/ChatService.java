package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.utils.PromptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class ChatService {
    private ChatClient chatClient;
    private VectorStore vectorStore;
    private RagService ragService;
    ChatService(OllamaChatModel ollamaChatModel, OpenAiChatModel openAiChatModel, PgVectorStore vectorStore, RagService ragService) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
        this.ragService = ragService;
    }


    public String getResponse(String query, String userId) {
        try {
            String context = ragService.generateContext(query, userId);
            log.info("description=\"fetched context successfully\" | contextLength={}", context.length());
            Prompt prompt = PromptUtil.getPromptForChatting(context, query, new HashMap<>());
            String response = chatClient.prompt(prompt)
                    .call()
                    .content();
            return response;
        } catch (Exception e) {
            log.error("failed to fetch the chat response", e);
            throw new RuntimeException(e);
        }
    }
}
