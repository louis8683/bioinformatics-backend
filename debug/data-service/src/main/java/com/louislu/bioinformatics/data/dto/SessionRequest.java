package com.louislu.bioinformatics.data.dto;

import com.louislu.bioinformatics.data.model.Session;

public record SessionRequest(
        Long id,
        String userId,
        String groupId,
        String sensorMac,
        String startTimestamp,
        String endTimestamp,
        String description
) {
//    public static SessionRequest from(Session session) {
//        return new SessionRequest(
//                session.getId(),
//                session.getUserId(),
//                session.getGroupId(),
//                session.getSensorMac(),
//                session.getStartTimestamp().toString(),
//                session.getEndTimestamp().toString(),
//                session.getDescription()
//        );
//    }
}


