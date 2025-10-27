package com.llm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class IngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    @Value("classpath:docs/Flexora_FAQ.pdf")
    private Resource faqPdf;

    private final PgVectorStore vectorStore;

    public IngestionService(@Qualifier(value = "qaVectorStore") PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        //ingestPdfDocs(faqPdf);
    }

    private void ingestPdfDocs(Resource faqPdf) {
        // Implementation for ingesting PDF documents goes here
        var reader = new PagePdfDocumentReader(faqPdf);
        var docs = reader.get();
        log.info("Ingesting {} pages from PDF document.", docs.size());
        this.vectorStore.add(docs);
        log.info("PDF document ingested successfully into the vector store.");
    }


}
