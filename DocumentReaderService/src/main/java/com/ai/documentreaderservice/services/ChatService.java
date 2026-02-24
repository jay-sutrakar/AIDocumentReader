package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.model.ChatMessage;
import com.ai.documentreaderservice.utils.PromptUtil;
import com.ai.documentreaderservice.utils.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ChatService {
    private final ChatClient chatClient;
    private final RagService ragService;
    private final JdbcTemplate jdbcTemplate;
    ChatService(OllamaChatModel ollamaChatModel, OpenAiChatModel openAiChatModel, PgVectorStore vectorStore, RagService ragService, JdbcTemplate jdbcTemplate) {
        this.chatClient = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
        this.ragService = ragService;
        this.jdbcTemplate = jdbcTemplate;
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

    public List<ChatMessage> getChatHistory(String documentId) {
       return jdbcTemplate.query(QueryUtil.GET_CHAT_HISTORY, (response, index) -> {
            return ChatMessage.builder()
                    .documentId(documentId)
                    .sessionId(response.getString("sesssion_id"))
                    .role(response.getString("role"))
                    .content(response.getString("content"))
                    .createdAt(response.getString("created_at"))
                    .build();
        }, documentId);
    }

    public void updateChatHistory(ChatMessage chatMessage) {
        jdbcTemplate.update(QueryUtil.CREATE_CHAT_HISTORY, chatMessage.getSessionId(), chatMessage.getDocumentId(),chatMessage.getRole(), chatMessage.getContent());
    }
}
