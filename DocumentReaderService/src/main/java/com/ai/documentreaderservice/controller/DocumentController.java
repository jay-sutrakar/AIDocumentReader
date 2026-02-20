package com.ai.documentreaderservice.controller;

import com.ai.documentreaderservice.services.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/document")
public class DocumentController {
    private final RagService ragService;
    DocumentController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/store")
    String uploadDocument() {
        ragService.storeDocument();
        return "Hello";
    }
}
