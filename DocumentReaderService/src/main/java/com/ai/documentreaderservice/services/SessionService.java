package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.utils.QueryUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SessionService {
    private JdbcTemplate jdbcTemplate;
    public SessionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String createOrGetSessionId(String userId) {
       UUID userUuid = userId == null ? null : UUID.fromString(userId);
       List<String> sessionId = jdbcTemplate.query(QueryUtil.CREATE_SESSION_METADATA, (res, idx) -> res.getString("id"), userUuid, LocalDateTime.now().plusDays(1));
        log.info("Successfully created session. | sessionId={}", sessionId);
        return sessionId.get(0);
    }

}
