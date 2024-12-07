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
                session.getStartTimestamp().toString(),
                session.getEndTimestamp().toString(),
                session.getDescription()
        );
    }
}


