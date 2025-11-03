package com.audit_service.service;

import com.audit_service.dto.EmployeeAuditHistoryDTO;
import com.audit_service.dto.FieldChangeDTO;
import com.audit_service.exception.AuditSerializationException;
import com.audit_service.model.AuditEvent;
import com.audit_service.repository.AuditEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;
    private final Javers javers;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public void logEvent(String domain, String entity, Long entityId, String eventName, String payload, String summary, Integer version, String initiator) {
        AuditEvent event = new AuditEvent();
        event.setEventDomain(domain);
        event.setEventEntity(entity);
        event.setEventEntityId(entityId);
        event.setEventName(eventName);
        event.setEventPayload(payload);
        event.setEventSummary(summary);
        event.setEventEntityVersion(version);
        event.setEventTimestamp(LocalDateTime.now());
        event.setEntryTimestamp(LocalDateTime.now());
        event.setEventInitiator(initiator);
        auditEventRepository.save(event);
    }

    /**
     * Log employee creation event
     */
    public void logEmployeeCreated(Object employee, String initiator) {
        try {
            String payload = objectMapper.writeValueAsString(employee);
            String summary = "Employee created";
            logEvent("HR", "EMPLOYEE", getEntityId(employee), "CREATED", payload, summary, 1, initiator);
        } catch (JsonProcessingException e) {
            throw new AuditSerializationException("Failed to serialize employee data for audit", e);
        }
    }

    /**
     * Log employee update event with old and new values
     */
    public void logEmployeeUpdated(Object oldEmployee, Object newEmployee, String initiator) {
        try {
            Map<String, Object> changePayload = new HashMap<>();
            changePayload.put("oldValue", oldEmployee);
            changePayload.put("newValue", newEmployee);
            changePayload.put("changes", detectChanges(oldEmployee, newEmployee));

            String payload = objectMapper.writeValueAsString(changePayload);
            String summary = generateUpdateSummary(oldEmployee, newEmployee);

            logEvent("HR", "EMPLOYEE", getEntityId(newEmployee), "UPDATED", payload, summary,
                    getVersion(newEmployee), initiator);
        } catch (JsonProcessingException e) {
            throw new AuditSerializationException("Failed to serialize employee data for audit", e);
        }
    }

    /**
     * Log employee deletion event
     */
    public void logEmployeeDeleted(Object employee, String initiator) {
        try {
            String payload = objectMapper.writeValueAsString(employee);
            String summary = "Employee deleted";
            logEvent("HR", "EMPLOYEE", getEntityId(employee), "DELETED", payload, summary,
                    getVersion(employee), initiator);
        } catch (JsonProcessingException e) {
            throw new AuditSerializationException("Failed to serialize employee data for audit", e);
        }
    }

    /**
     * Detect changes between old and new employee objects
     */
    private Map<String, Map<String, Object>> detectChanges(Object oldObj, Object newObj) {
        try {
            Map<String, Object> oldMap = objectMapper.convertValue(oldObj, Map.class);
            Map<String, Object> newMap = objectMapper.convertValue(newObj, Map.class);
            Map<String, Map<String, Object>> changes = new HashMap<>();

            for (String key : newMap.keySet()) {
                Object oldValue = oldMap.get(key);
                Object newValue = newMap.get(key);

                if (oldValue == null && newValue != null) {
                    Map<String, Object> change = new HashMap<>();
                    change.put("old", null);
                    change.put("new", newValue);
                    changes.put(key, change);
                } else if (oldValue != null && !oldValue.equals(newValue)) {
                    Map<String, Object> change = new HashMap<>();
                    change.put("old", oldValue);
                    change.put("new", newValue);
                    changes.put(key, change);
                }
            }
            return changes;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Generate human-readable summary of changes
     */
    private String generateUpdateSummary(Object oldObj, Object newObj) {
        Map<String, Map<String, Object>> changes = detectChanges(oldObj, newObj);
        if (changes.isEmpty()) {
            return "Employee updated - no changes detected";
        }

        StringBuilder summary = new StringBuilder("Employee updated: ");
        changes.keySet().forEach(field -> {
            if (!field.equals("id") && !field.equals("updatedTimestamp") && !field.equals("createdTimestamp")) {
                summary.append(field).append(", ");
            }
        });

        String result = summary.toString();
        return result.endsWith(", ") ? result.substring(0, result.length() - 2) : result;
    }

    /**
     * Extract entity ID from employee object using reflection
     */
    private Long getEntityId(Object obj) {
        try {
            return (Long) obj.getClass().getMethod("getId").invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get version number from employee object
     */
    private Integer getVersion(Object obj) {
        try {
            return (Integer) obj.getClass().getMethod("getVersion").invoke(obj);
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Get all audit events
     */
    public List<AuditEvent> getAllAuditEvents() {
        return auditEventRepository.findAll();
    }

    /**
     * Get audit events for a specific employee
     */
    public List<AuditEvent> getAuditEventsByEmployeeId(Long employeeId) {
        return auditEventRepository.findByEventEntityIdOrderByEventTimestampDesc(employeeId);
    }

    /**
     * Get audit events by entity type (e.g., "EMPLOYEE")
     */
    public List<AuditEvent> getAuditEventsByEntity(String entity) {
        return auditEventRepository.findRecentAuditEvents(entity);
    }

    /**
     * Get audit events by event name (e.g., "CREATED", "UPDATED", "DELETED")
     */
    public List<AuditEvent> getAuditEventsByEventName(String eventName) {
        return auditEventRepository.findByEventName(eventName);
    }

    /**
     * Get audit events by initiator
     */
    public List<AuditEvent> getAuditEventsByInitiator(String initiator) {
        return auditEventRepository.findByEventInitiator(initiator);
    }

    /**
     * Get audit events within a date range
     */
    public List<AuditEvent> getAuditEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditEventRepository.findByEventTimestampBetween(startDate, endDate);
    }

    /**
     * Get audit events for a specific employee and entity type
     */
    public List<AuditEvent> getAuditEventsByEntityAndId(String entity, Long entityId) {
        return auditEventRepository.findByEventEntityAndEventEntityId(entity, entityId);
    }

    /**
     * Get employee audit history with parsed field changes
     */
    public List<EmployeeAuditHistoryDTO> getEmployeeAuditHistory(Long employeeId) {
        List<AuditEvent> auditEvents = auditEventRepository.findByEventEntityIdOrderByEventTimestampDesc(employeeId);

        return auditEvents.stream().map(event -> {
            EmployeeAuditHistoryDTO dto = new EmployeeAuditHistoryDTO();
            dto.setEmployeeId(employeeId);
            dto.setEventName(event.getEventName());
            dto.setEventTimestamp(event.getEventTimestamp());
            dto.setEventInitiator(event.getEventInitiator());
            dto.setVersion(event.getEventEntityVersion());

            try {
                if ("UPDATED".equals(event.getEventName())) {
                    // Parse the payload to extract changes
                    Map<String, Object> payload = objectMapper.readValue(
                        event.getEventPayload(),
                        new TypeReference<Map<String, Object>>() {}
                    );

                    // Extract changes
                    Map<String, Map<String, Object>> changes = (Map<String, Map<String, Object>>) payload.get("changes");
                    if (changes != null) {
                        List<FieldChangeDTO> fieldChanges = changes.entrySet().stream()
                            .map(entry -> FieldChangeDTO.from(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
                        dto.setChanges(fieldChanges);
                    }

                    // Set current state (newValue)
                    dto.setCurrentState(payload.get("newValue"));
                } else {
                    // For CREATED or DELETED, the payload is the employee object itself
                    Object employeeData = objectMapper.readValue(
                        event.getEventPayload(),
                        Object.class
                    );
                    dto.setCurrentState(employeeData);
                }
            } catch (JsonProcessingException e) {
                // If parsing fails, set changes as empty
                dto.setChanges(new ArrayList<>());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Get specific field changes for an employee
     */
    public List<FieldChangeDTO> getEmployeeFieldChanges(Long employeeId, String fieldName) {
        List<AuditEvent> auditEvents = auditEventRepository.findByEventEntityIdOrderByEventTimestampDesc(employeeId);

        return auditEvents.stream()
            .filter(event -> "UPDATED".equals(event.getEventName()))
            .flatMap(event -> {
                try {
                    Map<String, Object> payload = objectMapper.readValue(
                        event.getEventPayload(),
                        new TypeReference<Map<String, Object>>() {}
                    );
                    Map<String, Map<String, Object>> changes = (Map<String, Map<String, Object>>) payload.get("changes");

                    if (changes != null && changes.containsKey(fieldName)) {
                        return List.of(FieldChangeDTO.from(fieldName, changes.get(fieldName))).stream();
                    }
                } catch (JsonProcessingException e) {
                    // Skip if parsing fails
                }
                return List.<FieldChangeDTO>of().stream();
            })
            .collect(Collectors.toList());
    }

    /**
     * Compare two objects and get JSON diff using JaVers (no database persistence)
     */
    public String compareObjectsAsJson(Object oldObject, Object newObject) {
        return javers.getJsonConverter().toJson(
                javers.compare(oldObject, newObject)
        );
    }

    public String getEmployeeJsonDiff() {
        String json1 = """
                {
                  "id": 6,
                  "firstName": "Ibney",
                  "lastName": "Ali",
                  "email": "ibney@gmail.com",
                  "phone": "1234567",
                  "department": {
                    "id": 1,
                    "addressLine1": "123 Main",
                    "addressLine2": "Apt 4B",
                    "addressLine3": null,
                    "country": "United States",
                    "postalCode": "10001",
                    "createdTimestamp": "2025-11-03T00:44:18.667828",
                    "updatedTimestamp": "2025-11-03T00:44:18.667828",
                    "updatedBy": "SYSTEM"
                  },
                  "address": {
                    "id": 1,
                    "name": "Information",
                    "createdTimestamp": "2025-11-03T00:44:18.665827",
                    "updatedTimestamp": "2025-11-03T00:44:18.665827",
                    "updatedBy": "SYSTEM"
                  },
                  "createdTimestamp": "2025-11-03T00:47:59.177571",
                  "updatedTimestamp": "2025-11-03T00:48:37.68652",
                  "updatedBy": "System",
                  "version": 1
                }""";

        String json2 = """
                {
                  "id": 6,
                  "firstName": "Ibney",
                  "lastName": "Ali",
                  "email": "ibneyali@gmail.com",
                  "phone": "1234567890",
                  "department": {
                    "id": 1,
                    "addressLine1": "123 Main Street",
                    "addressLine2": "Apt 4B",
                    "addressLine3": null,
                    "country": "United States",
                    "postalCode": "10001",
                    "createdTimestamp": "2025-11-03T00:44:18.667828",
                    "updatedTimestamp": "2025-11-03T00:44:18.667828",
                    "updatedBy": "SYSTEM"
                  },
                  "address": {
                    "id": 1,
                    "name": "Information Technology",
                    "createdTimestamp": "2025-11-03T00:44:18.665827",
                    "updatedTimestamp": "2025-11-03T00:44:18.665827",
                    "updatedBy": "SYSTEM"
                  },
                  "createdTimestamp": "2025-11-03T00:47:59.177571",
                  "updatedTimestamp": "2025-11-03T00:48:37.68652",
                  "updatedBy": "System",
                  "version": 1
                }""";

        try {
            // Parse JSON strings into Map objects so JaVers can compare them
            Map<String, Object> obj1 = objectMapper.readValue(json1, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> obj2 = objectMapper.readValue(json2, new TypeReference<Map<String, Object>>() {});

            // Now compare the Map objects
            return javers.getJsonConverter().toJson(javers.compare(obj1, obj2));
        } catch (JsonProcessingException e) {
            throw new AuditSerializationException("Failed to parse JSON for comparison", e);
        }
    }
}
