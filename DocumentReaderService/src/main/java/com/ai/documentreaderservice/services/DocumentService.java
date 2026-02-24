package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.model.DocumentMetadata;
import com.ai.documentreaderservice.utils.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocumentService {
    private final JdbcTemplate jdbcTemplate;
    public DocumentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String uploadDocumentMetadata(String fileName, String userId) {
       String documentId = jdbcTemplate.queryForObject(QueryUtil.CREATE_DOCUMENT_METADATA, (res, idx) -> {
           return res.getString("id");
       },userId, fileName);
       log.info("document metadata uploaded successfully.");
       return documentId;
    }

    public List<DocumentMetadata> getDocuments(String userId) {
        List<DocumentMetadata> documentMetadataList = jdbcTemplate.query(QueryUtil.CREATE_DOCUMENT_METADATA, (res, idx) -> {
            return DocumentMetadata.builder()
                    .fileName(res.getString("file_name"))
                    .id(res.getString("id"))
                    .createdAt(res.getString("created_at"))
                    .build();
        }, userId);
        log.info("fetched document metadata list");
        return documentMetadataList;
    }
}
