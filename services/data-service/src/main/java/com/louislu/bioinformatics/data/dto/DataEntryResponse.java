package com.louislu.bioinformatics.data.dto;

import com.louislu.bioinformatics.data.model.DataEntry;

import java.math.BigDecimal;

public record DataEntryResponse (
        Long id,
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
    public static DataEntryResponse from(DataEntry dataEntry) {
        return new DataEntryResponse(
                dataEntry.getId(),
                dataEntry.getUserId(),
                dataEntry.getSession().getId(),
                dataEntry.getTimestamp().toString(),
                dataEntry.getLatitude(),
                dataEntry.getLongitude(),
                dataEntry.getCoLevel(),
                dataEntry.getPm2_5Level(),
                dataEntry.getTemperature(),
                dataEntry.getHumidity()
        );
    }
}
