package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.model.DocumentMetadata;
import com.ai.documentreaderservice.utils.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DocumentService {
    private final JdbcTemplate jdbcTemplate;
    public DocumentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String uploadDocumentMetadata(String fileName, String userId, String sessionId) {
       UUID userUUId = userId == null ? null : UUID.fromString(userId);
       UUID sessionUUId = sessionId == null ? null : UUID.fromString(sessionId);
       String documentId = jdbcTemplate.queryForObject(QueryUtil.CREATE_DOCUMENT_METADATA, (res, idx) ->
            res.getString("id")
       , userUUId, sessionUUId, fileName);
       log.info("document metadata uploaded successfully. | userId={} | sessionId={}", userId, sessionId);
       return documentId;
    }

    public List<DocumentMetadata> getDocuments(String userId, String sessionId) {
        UUID sessionUUid = sessionId == null ? null : UUID.fromString(sessionId);
        UUID userUUid = userId == null ? null : UUID.fromString(userId);
        List<DocumentMetadata> documentMetadataList = jdbcTemplate.query(QueryUtil.GET_USER_DOCUMENTS, (res, idx) -> {
            return DocumentMetadata.builder()
                    .fileName(res.getString("file_name"))
                    .id(res.getString("id"))
                    .userId(userId)
                    .sessionId(res.getString("session_id"))
                    .createdAt(res.getString("created_at"))
                    .build();
        }, userUUid, sessionUUid);
        log.info("fetched document metadata list");
        return documentMetadataList;
    }
}
