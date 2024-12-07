package com.louislu.bioinformatics.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.TimestampWithTimeZoneJdbcType;

import java.time.OffsetDateTime;

@Entity
@Table(name = "sessions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userId;
    private String groupId;
    private String sensorMac;
    @JdbcType(TimestampWithTimeZoneJdbcType.class)
    private OffsetDateTime startTimestamp;
    @JdbcType(TimestampWithTimeZoneJdbcType.class)
    private OffsetDateTime endTimestamp;
    private String description;
}
