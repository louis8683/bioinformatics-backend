package com.louislu.bioinformatics.data.dto;

import com.louislu.bioinformatics.data.model.Session;

public record SessionResponse(
        Long id,
        String userId,
        String groupId,
        String sensorMac,
        String startTimestamp,
        String endTimestamp,
        String description
) {
    public static SessionResponse from(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getUserId(),
                session.getGroupId(),
                session.getSensorMac(),
                session.getStartTimestamp() != null? session.getStartTimestamp().toString() : null,
                session.getEndTimestamp() != null? session.getEndTimestamp().toString() : null,
                session.getDescription()
        );
    }
}


