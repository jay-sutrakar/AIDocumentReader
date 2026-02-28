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
    private final RagService ragService;
    public DocumentService(JdbcTemplate jdbcTemplate, RagService ragService) {
        this.jdbcTemplate = jdbcTemplate;
        this.ragService = ragService;
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

    public void deleteDocument(String userId, String documentId) {
        if (userId == null) {
            throw new RuntimeException("User id is required to delete the document.");
        }
        if (documentId == null) {
            throw new RuntimeException("Document id is required to delete the document.");
        }

        ragService.deleteDocumentEmbeddings(documentId);
        jdbcTemplate.query(QueryUtil.DELETE_CHAT_HISTORY, (row, idx) -> idx, documentId);
        jdbcTemplate.query(QueryUtil.DELETE_DOCUMENT_METADATA, (row, idx) -> idx, documentId);
    }
}
