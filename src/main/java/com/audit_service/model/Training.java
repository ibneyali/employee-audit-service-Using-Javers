package com.audit_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.LocalDateTime;

@Entity
@Table(name = "TRAINING")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeName("Training")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED_TIMESTAMP", nullable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "UPDATED_TIMESTAMP", nullable = false)
    private LocalDateTime updatedTimestamp;

    @Column(name = "UPDATED_BY")
    private String updatedBy;
}
