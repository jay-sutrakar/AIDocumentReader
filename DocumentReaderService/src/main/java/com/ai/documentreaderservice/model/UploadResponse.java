package com.ai.documentreaderservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResponse {
    private String userId;
    private String sessionId;
}
