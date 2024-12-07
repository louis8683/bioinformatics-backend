package com.louislu.bioinformatics.data.dto;

import com.louislu.bioinformatics.data.model.DataEntry;
import com.louislu.bioinformatics.data.model.Session;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DataEntryRequest(
        String userId,
        Long sessionId,
        String timestamp,
        BigDecimal latitude,
        BigDecimal longitude,
        float coLevel,
        float pm2_5Level,
        float temperature,
        float humidity
) {
    public DataEntry toDataEntry(Session session) {
        return DataEntry.builder()
                .userId(userId)
                .session(session)
                .timestamp(OffsetDateTime.parse(timestamp))
                .latitude(latitude)
                .longitude(longitude)
                .coLevel(coLevel)
                .pm2_5Level(pm2_5Level)
                .temperature(temperature)
                .humidity(humidity)
                .build();
    }
}

