package com.ai.documentreaderservice.model;

public record ChatRequest(String documentId, String sessionId, String message) {}
