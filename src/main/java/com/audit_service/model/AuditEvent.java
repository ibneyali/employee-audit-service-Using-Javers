package com.audit_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUDIT_TABLE")
@Data
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventDomain;
    private String eventEntity;
    private Long eventEntityId;
    private String eventName;
    @Lob
    private String eventPayload;
    private String eventSummary;
    private Integer eventEntityVersion;
    private LocalDateTime eventTimestamp;
    private LocalDateTime entryTimestamp;
    private String eventInitiator;
}
