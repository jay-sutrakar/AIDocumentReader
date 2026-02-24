package com.ai.documentreaderservice.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DocumentMetadata {
    private String id;
    private String fileName;
    private String createdAt;
}
