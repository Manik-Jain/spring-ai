package com.llm.controller;

import com.llm.service.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class IngestionController {

    private static final Logger log = LoggerFactory.getLogger(IngestionController.class);

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    //@RequestParam("file") MultipartFile file
    @PostMapping("/ingest/pdf")
    public ResponseEntity<String> ingestPdf() {
        try {
            //ingestionService.ingestPdfDocs();
            return ResponseEntity.ok("PDF ingested successfully.");
        } catch (Exception e) {
            log.error("Error ingesting PDF: ", e);
            return ResponseEntity.status(500).body("Failed to ingest PDF.");
        }
    }
}
