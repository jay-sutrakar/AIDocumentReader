package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.utils.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public String createUser(String email) {
        if (userDoesNotExists(email)) {
            String userId = jdbcTemplate.queryForObject(QueryUtil
                    .CREATE_USER_QUERY,(res, idx) -> {
                return res.getString("id");
            }, email);
            return userId;
        }
        throw new RuntimeException("User already exists.");
    }
    public String getUser(String email) {
        Map<String, Object> userInfo = jdbcTemplate.queryForMap(QueryUtil.GET_USER_QUERY, email);
        if (userInfo.isEmpty()) {
            log.error("User info isn't available.");
            throw new RuntimeException("User doesn't exists!");
        }
        return userInfo.get("id").toString();
    }

    private boolean userDoesNotExists(String email) {
        try {
            getUser(email);
            return false;
        } catch (Exception e) {
            return true;
        }

    }
}
