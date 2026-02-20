package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.components.PdfDocumentReader;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class RagService {
    private OllamaEmbeddingModel ollamaEmbeddingModel;
    private PdfDocumentReader pdfDocumentReader;

    @Value("classpath:/sample.pdf")
    Resource pdfFile;
    public RagService(OllamaEmbeddingModel ollamaEmbeddingModel, PdfDocumentReader pdfDocumentReader) {
        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
        this.pdfDocumentReader = pdfDocumentReader;
    }

    public void storeDocument() {
        pdfDocumentReader.readAndStoreDocument(pdfFile);
    }
}
