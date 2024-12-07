package com.louislu.bioinformatics.data.dto;

import com.louislu.bioinformatics.data.model.Session;

public record SessionRequest(
        String userId,
        String groupId,
        String sensorMac,
        String description
) {
    public Session toSession() {
        if (userId() == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (sensorMac() == null) {
            throw new IllegalArgumentException("sensorMac cannot be null");
        }
        return Session.builder()
                .userId(userId())
                .groupId(groupId())
                .sensorMac(sensorMac())
                .description(description())
                .build();
    }
}


