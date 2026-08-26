package com.resumebuilder.config.jackson;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Job descriptions, resume text, etc. are often pasted as raw multi-line text
     * into a JSON string field without escaping newlines (\n). Strict JSON parsing
     * rejects that as an "unquoted control character". This relaxes just that one
     * rule so unescaped newlines/tabs inside string values are accepted instead of
     * failing the whole request with a 400.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonBuilderCustomizer() {
        return builder -> builder.featuresToEnable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
    }
}