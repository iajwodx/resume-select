package com.resume.service;

import java.io.InputStream;

/**
 * Service for parsing PDF files and extracting text content.
 */
public interface PdfParseService {

    /**
     * Extract text content from a PDF input stream.
     *
     * @param inputStream the PDF file input stream
     * @return extracted text content
     * @throws Exception if parsing fails
     */
    String extractText(InputStream inputStream) throws Exception;
}
