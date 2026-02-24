package com.ai.documentreaderservice.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private JdbcTemplate jdbcTemplate;
    public SessionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String createOrGetSessionId(HttpServletRequest request, String userId) {
        String sessionId = request.getSession().getId();
        if (sessionId == null) {
            sessionId = userId;
        }
        return sessionId;
    }

}
