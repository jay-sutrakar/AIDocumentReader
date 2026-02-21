package com.ai.documentreaderservice.services;

import com.ai.documentreaderservice.components.PdfDocumentReader;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;

@Service
public class RagService {
    private PdfDocumentReader pdfDocumentReader;

    public RagService(PdfDocumentReader pdfDocumentReader) {
        this.pdfDocumentReader = pdfDocumentReader;
    }

    public void storeDocument(Path filePath, Map<String, Object> additionalMetadata) {
        Resource resource = FileUrlResource.from(filePath.toUri());
        pdfDocumentReader.readAndStoreDocument(resource, additionalMetadata);
    }


}
