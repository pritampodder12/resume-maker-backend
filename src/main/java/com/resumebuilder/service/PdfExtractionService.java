package com.resumebuilder.service;

import com.resumebuilder.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class PdfExtractionService {
    public String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new BadRequestException(
                        "Could not extract text from PDF.");
            }

            log.debug("Extracted {} characters from PDF", text.length());
            return text;

        } catch (IOException e) {
            log.error("Failed to parse PDF file", e);
            throw new BadRequestException("Failed to read PDF file.");
        }
    }
}
