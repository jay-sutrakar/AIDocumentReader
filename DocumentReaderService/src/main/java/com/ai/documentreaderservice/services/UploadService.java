package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.model.UploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
@Service
public class UploadService {

    private final RagService ragService;
    public UploadService(RagService ragService) {
        this.ragService = ragService;
    }

    public UploadResponse uploadDocument(MultipartFile file, String userId) {
        Path tempPath = null;
        try {
            validateFile(file);
            if (!StringUtils.hasText(userId)) {
                throw new RuntimeException("Userid can not be empty.");
            }
            tempPath = Files.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempPath);
            String sessionId = UUID.randomUUID().toString();
            Map<String, Object> additionalMetadata = new HashMap<>();
            additionalMetadata.put("userId", userId);
            additionalMetadata.put("sessionId", sessionId);
            ragService.storeDocument(tempPath, additionalMetadata);
            return UploadResponse.builder()
                    .userId(userId)
                    .sessionId(sessionId)
                    .build();
        } catch (Exception e) {
            log.error("className={}", this.getClass().getSimpleName(),e);
            throw new RuntimeException(e);
        } finally {
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException e) {
                log.error("description=\"failed to delete temp file.\"", e);
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File length is greater than 5 MB : fileSize=" + file.getSize());
        }
    }
}
