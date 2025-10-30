package com.audit_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee")
@TypeName("Employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;

    @Column(name = "ADDRESS_ID")
    private Long addressId;

    @Column(name = "CREATED_TIMESTAMP", nullable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "UPDATED_TIMESTAMP", nullable = false)
    private LocalDateTime updatedTimestamp;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Version
    @Column(name = "VERSION")
    private Integer version;
}
