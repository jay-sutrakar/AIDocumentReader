package com.ai.documentreaderservice.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfDocumentReader {
    private static final Logger log = LoggerFactory.getLogger(PdfDocumentReader.class);
    private final VectorStore vectorStore;

    public PdfDocumentReader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }
    public void readAndStoreDocument(Resource resource) {
        List<Document> documents = getDocuments(resource);
        List<Document> splittedDocument = textSplitter(documents);
        vectorStore.accept( splittedDocument);
        log.info("Document stored success fully | vectorStoreName={}", vectorStore.getName());
    }

    public List<Document> textSplitter(List<Document> documents) {
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        return tokenTextSplitter.split(documents);
    }
    public List<Document> getDocuments(Resource resource) {
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build());
        return pagePdfDocumentReader.read();
    }
}
