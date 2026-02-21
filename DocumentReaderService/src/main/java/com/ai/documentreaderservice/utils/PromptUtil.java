package com.ai.documentreaderservice.utils;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;

import java.util.List;
import java.util.Map;

public class PromptUtil {
    private static final String userText = """
        Answer briefly based on context only. 
        Respond "Unsure about answer" if not sure about the answer.
        Context: {context}
        Question: {query}.
    """;

    private static final String systemText = """
        You are a helpful AI. Keep responses concise. .
    """;

    public static Prompt getPromptForChatting(String context, String query, Map<String, Object> parameters) {

        // 1. Create templates (no placeholders in system for now)
        PromptTemplate userTemplate = new PromptTemplate(userText);
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemText);

        // 2. Render with context/query values
        Message userMessage = userTemplate.createMessage(
                Map.of("context", context, "query", query)
        );

        Message systemMessage = systemTemplate.createMessage();  // e.g. {name, voice}

        // 3. Combine into Prompt
        return new Prompt(List.of(systemMessage, userMessage));
    }

}
