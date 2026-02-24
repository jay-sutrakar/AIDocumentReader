package com.ai.documentreaderservice.model;

import lombok.Builder;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatMessage {
    private String sessionId;
    private String documentId;
    private String role;
    private String content;
    private String createdAt;
}
