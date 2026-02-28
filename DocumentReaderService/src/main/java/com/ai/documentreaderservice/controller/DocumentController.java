package com.ai.documentreaderservice.controller;

import com.ai.documentreaderservice.model.*;
import com.ai.documentreaderservice.services.ChatService;
import com.ai.documentreaderservice.services.DocumentService;
import com.ai.documentreaderservice.services.SessionService;
import com.ai.documentreaderservice.services.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/document")
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {
    private final UploadService uploadService;
    private final ChatService chatService;
    private final SessionService sessionService;
    private final DocumentService documentService;

    DocumentController(UploadService uploadService, ChatService chatService, SessionService sessionService, DocumentService documentService) {
        this.chatService = chatService;
        this.uploadService = uploadService;
        this.sessionService = sessionService;
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam(value = "file", required = true) MultipartFile file, @RequestHeader(value = "userId", required = false) String userId, @RequestHeader(value = "sessionId", required = false) String sessionId) {
        if (file.isEmpty()) {
            log.error("file is empty!");
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
        try {
            if (sessionId == null)
                sessionId = sessionService.createOrGetSessionId(userId);
            UploadResponse uploadResponse = uploadService.uploadDocument(file, userId, sessionId);
            log.info("logType=tracking | userId={} | documentId={}", uploadResponse.getUserId(), uploadResponse.getDocumentId());
            return ResponseEntity
                    .ok(uploadResponse);
        } catch (Exception e) {
            log.error("failed to upload document", e);
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error_message", e.getMessage()));
        }

    }
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> chat(@RequestBody ChatRequest chatRequest, @RequestHeader(value = "userId") String userId) {
        long startTime = System.currentTimeMillis();
        try {
            if (!StringUtils.hasText(chatRequest.message()) || chatRequest.documentId() == null) {
                return ResponseEntity.badRequest().build();
            }

            String response = chatService.getResponse(chatRequest.message(), userId, chatRequest.sessionId());
            ChatMessage userChatMessage = ChatMessage
                    .builder()
                    .role("user")
                    .content(chatRequest.message())
                    .documentId(chatRequest.documentId())
                    .sessionId(chatRequest.sessionId())
                    .build();
            ChatMessage aiChatMessage = ChatMessage
                    .builder()
                    .role("ai")
                    .content(response)
                    .sessionId(chatRequest.sessionId())
                    .documentId(chatRequest.documentId())
                    .build();
            chatService.updateChatHistory(userChatMessage);
            chatService.updateChatHistory(aiChatMessage);
            log.info("description=\"fetched response successfully\" | duration={}", System.currentTimeMillis() - startTime);
            return ResponseEntity
                    .ok().body(new ChatResponse(response));
        } catch (Exception e) {
            log.error("failed to create chat!", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error_message", e.getMessage()));
        }
    }
    @GetMapping(value = "/chat-history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getChatHistory(@RequestParam(value = "documentId") String documentId) {
        try {
            List<ChatMessage> chatHistories = chatService.getChatHistory(documentId);
            log.info("Successfully fetched chat history");
            return ResponseEntity.ok(chatHistories);
        } catch (Exception e) {
            log.error("failed to fetch chat history.", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("user_message", e.getMessage()));
        }
    }

    @GetMapping(value = "/uploaded-documents", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUploadedDocuments(@RequestParam(value = "userId", required = false) String userId, @RequestParam(value = "sessionId", required = false) String sessionId) {
        try {
            if (userId == null && sessionId == null) {
                return ResponseEntity.badRequest().build();
            }
            List<DocumentMetadata> documentMetadataList = documentService.getDocuments(userId, sessionId);
            return ResponseEntity.ok(documentMetadataList);
        } catch (Exception e) {
            log.error("failed to fetch uploaded document list", e);
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error_message", e.getMessage()));
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteDocument(@RequestParam(value = "documentId") String documentId, @RequestParam(value = "userId") String userId) {
        try {
            documentService.deleteDocument(userId, documentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error_message", e.getMessage()));
        }
    }
}
