package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.utils.QueryUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService {
    private JdbcTemplate jdbcTemplate;
    public SessionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String createOrGetSessionId(HttpServletRequest request, String userId) {
        String sessionId = request.getSession().getId();

        if (sessionId != null) {
            return sessionId;
        }
        UUID userUuid = userId == null ? null : UUID.fromString(userId);
        jdbcTemplate.query(QueryUtil.CREATE_SESSION_METADATA, (res, idx) -> res.getString("id"), userUuid, LocalDateTime.now().plusDays(1));
        return sessionId;
    }

}
