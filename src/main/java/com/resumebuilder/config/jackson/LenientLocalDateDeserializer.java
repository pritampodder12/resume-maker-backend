package com.resumebuilder.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LenientLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();

        if (value == null || value.isBlank()) {
            return null;
        }

        value = value.trim();

        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE); // YYYY-MM-DD
        } catch (DateTimeParseException e) {
            // Catches "Present", "Current", "Ongoing", "N/A", garbage text, etc.
            // Instead of throwing, just treat it as "no date"
            return null;
        }
    }
}