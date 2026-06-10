package com.resume.service.impl;

import com.resume.service.PdfParseService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Implementation of PdfParseService using Apache PDFBox.
 */
@Service
public class PdfParseServiceImpl implements PdfParseService {

    private static final Logger log = LoggerFactory.getLogger(PdfParseServiceImpl.class);

    @Override
    public String extractText(InputStream inputStream) throws Exception {
        Path tempFile = Files.createTempFile("resume_", ".pdf");
        try {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            try (PDDocument document = Loader.loadPDF(tempFile.toFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                String text = stripper.getText(document);
                log.info("Extracted {} characters from PDF", text.length());
                return text;
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
