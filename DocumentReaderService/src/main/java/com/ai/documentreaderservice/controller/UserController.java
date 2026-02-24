package com.ai.documentreaderservice.controller;

import com.ai.documentreaderservice.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(@RequestParam(value = "email") String email) {
        if (!StringUtils.hasText(email)) {
            return ResponseEntity
                    .badRequest()
                    .body("User email can not be empty!");
        }
        try {
            String userId = userService.createUser(email);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}
