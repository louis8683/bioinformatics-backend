package com.louislu.bioinformatics.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.TimestampWithTimeZoneJdbcType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "data_entries")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DataEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userId;
    @ManyToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_dataentry_session"))
    private Session session;
    @JdbcType(TimestampWithTimeZoneJdbcType.class)
    private OffsetDateTime timestamp;
    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;
    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;
    private float coLevel;
    private float pm2_5Level;
    private float temperature;
    private float humidity;
}
