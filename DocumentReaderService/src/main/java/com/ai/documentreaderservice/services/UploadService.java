package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.model.DocumentMetadata;
import com.ai.documentreaderservice.model.UploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
@Service
public class UploadService {

    private final RagService ragService;
    private final DocumentService documentService;
    public UploadService(RagService ragService, DocumentService documentService) {
        this.ragService = ragService;
        this.documentService = documentService;
    }

    public UploadResponse uploadDocument(MultipartFile file, String userId) {
        Path tempPath = null;
        try {
            validateFile(file);
            if (!StringUtils.hasText(userId)) {
                log.error("Userid is not present.");
                throw new RuntimeException("Userid can not be empty.");
            }
            tempPath = Files.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempPath);
            String documentId = documentService.uploadDocumentMetadata(userId, file.getOriginalFilename());
            Map<String, Object> additionalMetadata = new HashMap<>();
            additionalMetadata.put("userId", userId);
            additionalMetadata.put("documentId", documentId);
            ragService.storeDocument(tempPath, additionalMetadata);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return UploadResponse.builder()
                    .userId(userId)
                    .documentId(documentId)
                    .fileName(file.getOriginalFilename())
                    .uploadedDate(LocalDate.now().format(formatter))
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
