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
import java.util.UUID;

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


    public String getResponse(String query, String userId, String sessionId) {
        try {
            String context = ragService.generateContext(query, userId, sessionId);
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
                    .sessionId(response.getString("session_id"))
                    .role(response.getString("role"))
                    .content(response.getString("content"))
                    .createdAt(response.getString("created_at"))
                    .build();
        }, UUID.fromString(documentId));
    }

    public void updateChatHistory(ChatMessage chatMessage) {
        try {
            UUID sessionId = UUID.fromString(chatMessage.getSessionId());
            UUID documentId = UUID.fromString(chatMessage.getDocumentId());
            jdbcTemplate.update(QueryUtil.CREATE_CHAT_HISTORY, sessionId, documentId, chatMessage.getRole(), chatMessage.getContent());
        } catch (Exception e) {
            log.error("failed to update chat history", e);
            throw new RuntimeException(e);
        }
    }
}
