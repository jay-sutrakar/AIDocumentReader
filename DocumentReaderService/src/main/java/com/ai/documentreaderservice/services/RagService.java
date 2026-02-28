package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.components.PdfDocumentReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.VectorStoreRetriever;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RagService {
    private PdfDocumentReader pdfDocumentReader;
    private VectorStoreRetriever vectorStoreRetriever;
    private VectorStore vectorStore;
    public RagService(PdfDocumentReader pdfDocumentReader, VectorStoreRetriever vectorStoreRetriever, VectorStore vectorStore) {
        this.pdfDocumentReader = pdfDocumentReader;
        this.vectorStoreRetriever = vectorStoreRetriever;
        this.vectorStore = vectorStore;
    }

    public void storeDocument(Path filePath, Map<String, Object> additionalMetadata) {
        try {
            Resource resource = FileUrlResource.from(filePath.toUri());
            pdfDocumentReader.readAndStoreDocument(resource, additionalMetadata);
        } catch (Exception e) {
            log.error("failed while storing document in vector store", e);
        }
    }

    public String generateContext(String query, String userId, String sessionId) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(1)
                .filterExpression(b.or(b.eq("userId", userId), b.eq("sessionId", sessionId)).build())
                .build();
        List<Document> documentList = vectorStoreRetriever.similaritySearch(searchRequest);
        return documentList.stream()
                .map(doc -> doc.getFormattedContent().substring(0, Math.min(1000, doc.getFormattedContent().length())))
                .collect(Collectors.joining("\n\n"));
    }

    public void deleteDocumentEmbeddings(String documentId) {
        try {
            FilterExpressionBuilder filterExpressionBuilder = new FilterExpressionBuilder();
            vectorStore.delete(filterExpressionBuilder.eq("documentId", documentId).build());
            log.info("successfully deleted document from vector store");
        } catch (Exception e) {
            log.error("failed to delete documents from vector store");
            throw new RuntimeException(e);
        }
    }

}
