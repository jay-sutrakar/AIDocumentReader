//package com.ai.documentreaderservice.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.io.IOException;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//class DocumentControllerTest {
//
//    @Autowired
////    private MockMvc mockMvc;
//
//    private MockMultipartFile pdfFile;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        // Create test PDF content (single page)
//        pdfFile = new MockMultipartFile(
//                "file",
//                "test-contract.pdf",
//                "application/pdf",
//                createTestPdfContent()
//        );
//    }
//
//    @Test
//    void shouldUploadPdfAndStoreChunks() throws Exception {
//
//        // When: Upload with userId
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
//                        .file(pdfFile)
//                        .param("userId", "user123")
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.totalPages").value(1))
//                .andExpect(jsonPath("$.totalChunks").value(3))
//                .andExpect(jsonPath("$.documentId").isNotEmpty());
//
//    }
//
//    @Test
//    void shouldRejectEmptyFile() throws Exception {
//        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.pdf",
//                "application/pdf", new byte[0]);
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
//                        .file(emptyFile)
//                        .param("userId", "user123"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("File is empty"));
//    }
//
//    @Test
//    void shouldHandleMissingUserId() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
//                        .file(pdfFile))
//                .andExpect(status().is4xxClientError());  // MethodArgumentNotFoundException
//    }
//
//    private byte[] createTestPdfContent() {
//        // Minimal PDF for testing (hex bytes)
//        return """
//            %PDF-1.4
//            1 0 obj
//            << /Type /Catalog /Pages 2 0 R >>
//            endobj
//            2 0 obj
//            << /Type /Pages /Kids [3 0 R] /Count 1 >>
//            endobj
//            3 0 obj
//            << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R >>
//            endobj
//            4 0 obj
//            << /Length 44 >>
//            stream
//            BT /F1 12 Tf 100 700 Td (Test contract PDF) Tj ET
//            endstream
//            endobj
//            xref
//            trailer << /Size 5 /Root 1 0 R >>
//            startxref
//            %%EOF
//            """.getBytes();
//    }
//}
