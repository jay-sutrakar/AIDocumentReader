package com.ai.documentreaderservice.controller;

import com.ai.documentreaderservice.model.UploadResponse;
import com.ai.documentreaderservice.services.ChatService;
import com.ai.documentreaderservice.services.RagService;
import com.ai.documentreaderservice.services.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/api/document")
public class DocumentController {
    private final UploadService uploadService;
    private final ChatService chatService;
    DocumentController(UploadService uploadService, ChatService chatService) {
        this.chatService = chatService;
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadDocument(@RequestParam(value = "file", required = true) MultipartFile file, @RequestHeader(value = "userId", required = true) String userId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
        try {
            UploadResponse uploadResponse = uploadService.uploadDocument(file, userId);
            log.info("logType=tracking | userId={} | sessionId={}", uploadResponse.getUserId(), uploadResponse.getSessionId());
            return ResponseEntity
                    .ok(uploadResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .build();
        }

    }
    @GetMapping("/chat")
    String chat() {
        return chatService.getResponse();
    }

}
