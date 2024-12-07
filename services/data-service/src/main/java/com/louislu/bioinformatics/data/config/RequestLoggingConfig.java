package com.louislu.bioinformatics.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true); // Logs client IP and session ID
        loggingFilter.setIncludeQueryString(true); // Logs query parameters
        loggingFilter.setIncludeHeaders(true); // Logs headers
        loggingFilter.setIncludePayload(true); // Logs request body
        loggingFilter.setMaxPayloadLength(10000); // Limit payload size in logs
        return loggingFilter;
    }
}
