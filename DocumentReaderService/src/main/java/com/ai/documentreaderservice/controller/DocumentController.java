package com.ai.documentreaderservice.controller;

import com.ai.documentreaderservice.model.*;
import com.ai.documentreaderservice.services.ChatService;
import com.ai.documentreaderservice.services.DocumentService;
import com.ai.documentreaderservice.services.SessionService;
import com.ai.documentreaderservice.services.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<UploadResponse> uploadDocument(HttpServletRequest request, @RequestParam(value = "file", required = true) MultipartFile file, @RequestHeader(value = "userId", required = false) String userId) {
        if (file.isEmpty()) {
            log.error("file is empty!");
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
        String sessionId = sessionService.createOrGetSessionId(request, userId);
        try {
            UploadResponse uploadResponse = uploadService.uploadDocument(file, userId);
            log.info("logType=tracking | userId={} | documentId={}", uploadResponse.getUserId(), uploadResponse.getDocumentId());
            return ResponseEntity
                    .ok(uploadResponse);
        } catch (Exception e) {
            log.error("failed to upload document", e);
            return ResponseEntity.internalServerError()
                    .build();
        }

    }
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponse> chat(String query, @RequestBody ChatRequest chatRequest, @RequestHeader(value = "userId") String userId) {
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
                    .build();
            ChatMessage aiChatMessage = ChatMessage
                    .builder()
                    .role("ai")
                    .content(response)
                    .documentId(chatRequest.documentId())
                    .build();
            chatService.updateChatHistory(userChatMessage);
            chatService.updateChatHistory(aiChatMessage);
            log.info("description=\"fetched response successfully\" | duration={}", System.currentTimeMillis() - startTime);
            return ResponseEntity
                    .ok().body(new ChatResponse(response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .build();
        }
    }
    @GetMapping(value = "/chat-history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ChatMessage>> getChatHistory(@RequestParam(value = "documentId") String documentId) {
        try {
            List<ChatMessage> chatHistories = chatService.getChatHistory(documentId);
            log.info("Successfully fetched chat history");
            return ResponseEntity.ok(chatHistories);
        } catch (Exception e) {
            log.error("failed to fetch chat history.", e);
            return ResponseEntity.internalServerError()
                    .build();
        }
    }

    @GetMapping(value = "/uploaded-documents", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DocumentMetadata>> getUploadedDocuments(@RequestParam(value = "userId") String userId) {
        try {
            List<DocumentMetadata> documentMetadataList = documentService.getDocuments(userId);
            return ResponseEntity.ok(documentMetadataList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
