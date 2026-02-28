package com.ai.documentreaderservice.controller;

import com.ai.documentreaderservice.model.AuthRequest;
import com.ai.documentreaderservice.services.SessionService;
import com.ai.documentreaderservice.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class UserController {
    private final UserService userService;
    private final SessionService sessionService;
    public UserController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody AuthRequest authRequest) {
        if (!StringUtils.hasText(authRequest.email())) {
            return ResponseEntity
                    .badRequest()
                    .body("User email can not be empty!");
        }
        try {
            String userId = userService.createUser(authRequest.email());
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            log.error("failed to create user", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error_message", e.getMessage()));
        }
    }

    @PostMapping(value = "/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String
            ,String>> fetchUser(@RequestBody AuthRequest authRequest) {
        if (!StringUtils.hasText(authRequest.email())) {
            return ResponseEntity.badRequest()
                    .build();
        }
        try {
            String userId = userService.getUser(authRequest.email());
            String sessionId = sessionService.createOrGetSessionId(userId);
            return ResponseEntity.ok(Map.of("userId", userId, "sessionId", sessionId));
        } catch (Exception e) {
            log.error("failed to fetch user info", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error_message", e.getMessage()));
        }
    }
}
