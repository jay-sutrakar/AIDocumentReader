package com.ai.documentreaderservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResponse {
    private String documentId;
    private String userId;
    private String fileName;
    private String uploadedDate;
    private String sessionId;
}
