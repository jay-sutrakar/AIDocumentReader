package com.ai.documentreaderservice.controller;

import com.ai.documentreaderservice.model.ChatRequest;
import com.ai.documentreaderservice.model.UploadResponse;
import com.ai.documentreaderservice.services.ChatService;
import com.ai.documentreaderservice.services.UploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/document")
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {
    private final UploadService uploadService;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    DocumentController(UploadService uploadService, ChatService chatService) {
        this.chatService = chatService;
        this.uploadService = uploadService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadDocument(@RequestParam(value = "file", required = true) MultipartFile file, @RequestHeader(value = "userId", required = true) String userId) {
        if (file.isEmpty()) {
            log.error("file is empty!");
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
            log.error("failed to upload document", e);
            return ResponseEntity.internalServerError()
                    .build();
        }

    }
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> chat(@RequestParam String query, @RequestBody ChatRequest chatRequest, @RequestHeader(value = "userId") String userId) {
        long startTime = System.currentTimeMillis();
        try {
            if ((chatRequest.message() == null || chatRequest.message().trim().isEmpty()) && query == null) {
                return ResponseEntity.badRequest().body("Empty message");
            }
            String message = query == null ? chatRequest.message() : query;
            String response = chatService.getResponse(message, userId);
            log.info("description=\"fetched response successfully\" | duration={}", System.currentTimeMillis() - startTime);
            return ResponseEntity
                    .ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .build();
        }
    }

}
