# JaVers Integration Documentation

## Overview

This project uses **JaVers** for comparing objects without database persistence. JaVers is configured to provide real-time object comparison capabilities only, not historical tracking.

## Current Implementation

### Architecture

```
┌─────────────────────────────────────────────────────────┐
│                 Employee Audit Service                   │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────────────┐         ┌──────────────────┐     │
│  │   JaVers Core    │         │  Custom Audit    │     │
│  │  (No Database)   │         │     System       │     │
│  └──────────────────┘         └──────────────────┘     │
│          │                            │                  │
│          │                            │                  │
│          ▼                            ▼                  │
│  ┌──────────────────┐         ┌──────────────────┐     │
│  │ Object Compare   │         │  AUDIT_TABLE     │     │
│  │  (Real-time)     │         │  (Historical)    │     │
│  └──────────────────┘         └──────────────────┘     │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### Configuration

**Dependency** (`build.gradle`):
```gradle
implementation 'org.javers:javers-core:7.6.2'
```

**Configuration** (`JaversConfig.java`):
```java
@Configuration
public class JaversConfig {
    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build();
    }
}
```

## What JaVers CAN Do (Without Database Persistence)

### ✅ Real-time Object Comparison

JaVers can compare two objects and show differences without storing anything to a database.

#### 1. Compare Two Entity Objects

```java
// Example: Compare two Employee objects
Employee oldEmployee = // ... get old employee state
Employee newEmployee = // ... get new employee state

String diff = auditService.compareObjectsAsJson(oldEmployee, newEmployee);
```

**Output Example:**
```json
{
  "changes": [
    {
      "changeType": "ValueChange",
      "globalId": {
        "entity": "com.audit_service.model.Employee",
        "cdoId": 1
      },
      "property": "email",
      "propertyChangeType": "PROPERTY_VALUE_CHANGED",
      "left": "john.doe@company.com",
      "right": "john.newemail@company.com"
    },
    {
      "changeType": "ValueChange",
      "property": "phone",
      "left": "1234567",
      "right": "1234567890"
    }
  ]
}
```

#### 2. Compare Two JSON Objects

```java
// Parse JSON strings to Map objects
Map<String, Object> obj1 = objectMapper.readValue(json1, 
    new TypeReference<Map<String, Object>>() {});
Map<String, Object> obj2 = objectMapper.readValue(json2, 
    new TypeReference<Map<String, Object>>() {});

// Compare
String diff = javers.getJsonConverter().toJson(javers.compare(obj1, obj2));
```

**Use Case:**
- Compare API request/response payloads
- Compare configuration files
- Compare data snapshots from different sources

#### 3. Deep Object Graph Comparison

JaVers automatically compares nested objects:

```java
// Employee with nested Department and Address
Employee oldEmployee = Employee.builder()
    .id(1L)
    .firstName("John")
    .department(department1)  // Nested object
    .address(address1)        // Nested object
    .build();

Employee newEmployee = Employee.builder()
    .id(1L)
    .firstName("John")
    .department(department2)  // Modified nested object
    .address(address2)        // Modified nested object
    .build();

// JaVers will detect changes in nested objects too
String diff = auditService.compareObjectsAsJson(oldEmployee, newEmployee);
```

## What JaVers CANNOT Do (Without Database Persistence)

### ❌ No Historical Tracking

Without committing to database, JaVers cannot:

| Feature | Status | Reason |
|---------|--------|--------|
| `javers.findChanges()` | ❌ Not Available | No data stored in JaVers repository |
| `javers.findSnapshots()` | ❌ Not Available | No snapshots saved |
| Query by author | ❌ Not Available | No commit metadata stored |
| Query by date range | ❌ Not Available | No timestamps recorded |
| View object history over time | ❌ Not Available | No historical data persisted |
| Track who made changes | ❌ Not Available | No commit author information |

### Example of What Won't Work:

```java
// ❌ This will NOT work - returns empty because nothing is committed
List<Change> changes = javers.findChanges(
    QueryBuilder.byInstanceId(employeeId, Employee.class).build()
);

// ❌ This will NOT work - returns empty
List<CdoSnapshot> snapshots = javers.findSnapshots(
    QueryBuilder.byInstanceId(employeeId, Employee.class).build()
);
```

## Custom Audit System (For Historical Tracking)

Since JaVers doesn't persist data, we use a **custom audit system** for historical tracking.

### ✅ What Custom Audit System Provides

| Feature | Implementation | Database Table |
|---------|---------------|----------------|
| Historical changes | ✅ Available | `AUDIT_TABLE` |
| Field-level tracking | ✅ Available | Stored in `EVENT_PAYLOAD` |
| Query by employee | ✅ Available | `findByEventEntityId()` |
| Query by date range | ✅ Available | `findByEventTimestampBetween()` |
| Query by initiator | ✅ Available | `findByEventInitiator()` |
| Event types | ✅ Available | CREATED, UPDATED, DELETED |

### Custom Audit APIs

#### Get Employee Audit History
```http
GET /api/audit/employee/{employeeId}/history
```

**Response:**
```json
[
  {
    "employeeId": 6,
    "eventName": "UPDATED",
    "eventTimestamp": "2025-11-03T00:48:37.686",
    "eventInitiator": "System",
    "version": 1,
    "changes": [
      {
        "fieldName": "email",
        "oldValue": "ibney@gmail.com",
        "newValue": "ibneyali@gmail.com"
      },
      {
        "fieldName": "phone",
        "oldValue": "1234567",
        "newValue": "1234567890"
      }
    ]
  },
  {
    "employeeId": 6,
    "eventName": "CREATED",
    "eventTimestamp": "2025-11-03T00:47:59.177",
    "eventInitiator": "System",
    "version": 0
  }
]
```

#### Get Specific Field Changes
```http
GET /api/audit/employee/{employeeId}/field/{fieldName}
```

Example:
```http
GET /api/audit/employee/6/field/email
```

**Response:**
```json
[
  {
    "fieldName": "email",
    "oldValue": "ibney@gmail.com",
    "newValue": "ibneyali@gmail.com"
  },
  {
    "fieldName": "email",
    "oldValue": "john@gmail.com",
    "newValue": "ibney@gmail.com"
  }
]
```

## Comparison: JaVers vs Custom Audit

| Feature | JaVers (Current) | Custom Audit System |
|---------|------------------|---------------------|
| **Real-time comparison** | ✅ Yes | ❌ No |
| **Historical tracking** | ❌ No (not persisted) | ✅ Yes |
| **Deep object comparison** | ✅ Yes | ⚠️ Manual implementation |
| **Query by date** | ❌ No | ✅ Yes |
| **Query by initiator** | ❌ No | ✅ Yes |
| **Field-level changes** | ✅ Yes (in diff) | ✅ Yes (stored) |
| **Database storage** | ❌ No | ✅ Yes (AUDIT_TABLE) |
| **JSON output** | ✅ Yes | ✅ Yes |
| **Nested object tracking** | ✅ Automatic | ⚠️ Manual |

## Use Cases

### When to Use JaVers (Current Implementation)

✅ **Use JaVers for:**
- Comparing two versions of an object in real-time
- Detecting what changed between old and new state
- Comparing JSON payloads from different sources
- Validation before saving changes
- Generating change reports on-the-fly
- Comparing configuration snapshots

**Example:**
```java
// Before saving, show user what will change
@PutMapping("/employees/{id}")
public ResponseEntity<?> updateEmployee(@PathVariable Long id, 
                                       @RequestBody Employee updatedEmployee) {
    Employee existing = employeeService.getEmployeeById(id).orElseThrow();
    
    // Show preview of changes
    String preview = auditService.compareObjectsAsJson(existing, updatedEmployee);
    
    // Then save and audit
    Employee saved = employeeService.updateEmployee(id, updatedEmployee);
    return ResponseEntity.ok(saved);
}
```

### When to Use Custom Audit System

✅ **Use Custom Audit for:**
- Viewing complete change history of an employee
- Compliance and regulatory requirements
- Audit trails for security
- Answering "who changed what when" questions
- Generating audit reports
- Forensic analysis

**Example:**
```java
// Get complete history
GET /api/audit/employee/123/history

// Find all changes by specific user
GET /api/audit/initiator/admin@company.com

// Get changes in date range
GET /api/audit/date-range?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
```

## Alternative: Full JaVers with Database Persistence

If you need JaVers to track historical changes, you would need to:

### Required Changes

1. **Update Dependency**
```gradle
// Replace
implementation 'org.javers:javers-core:7.6.2'

// With
implementation 'org.javers:javers-spring-boot-starter-sql:7.6.2'
```

2. **Add Commits in Services**
```java
@Transactional
public Employee updateEmployee(Long id, Employee updatedEmployee) {
    Employee existing = // ... get existing
    // ... update fields
    Employee saved = employeeRepository.save(existing);
    
    // Commit to JaVers database
    javers.commit(saved.getUpdatedBy(), saved);
    
    return saved;
}
```

3. **JaVers Creates Tables Automatically**
```sql
-- JaVers will create these tables:
jv_global_id
jv_commit
jv_commit_property
jv_snapshot
```

4. **Then You Can Query History**
```java
// Find all changes for an employee
List<Change> changes = javers.findChanges(
    QueryBuilder.byInstanceId(employeeId, Employee.class).build()
);

// Get snapshots over time
List<CdoSnapshot> snapshots = javers.findSnapshots(
    QueryBuilder.byInstanceId(employeeId, Employee.class).build()
);
```

### Trade-offs

| Aspect | JaVers Core (Current) | JaVers SQL (Full) |
|--------|----------------------|-------------------|
| Database tables | 1 (AUDIT_TABLE) | 5 (AUDIT_TABLE + 4 JaVers tables) |
| Storage overhead | Low | Higher |
| Query capabilities | Custom implementation | Rich JaVers queries |
| Historical tracking | Custom implementation | Built-in |
| Complexity | Lower | Higher |
| Performance | Faster | Slower (more tables) |

## API Examples

### Compare Two JSON Objects

**Endpoint:**
```http
GET /api/audit/employee/diff
```

**Implementation:**
```java
public String getEmployeeJsonDiff() {
    String json1 = "{ ... }";  // Old state
    String json2 = "{ ... }";  // New state
    
    Map<String, Object> obj1 = objectMapper.readValue(json1, 
        new TypeReference<Map<String, Object>>() {});
    Map<String, Object> obj2 = objectMapper.readValue(json2, 
        new TypeReference<Map<String, Object>>() {});
    
    return javers.getJsonConverter().toJson(javers.compare(obj1, obj2));
}
```

**Response:**
```json
{
  "changes": [
    {
      "changeType": "ValueChange",
      "globalId": {
        "valueObject": "java.util.LinkedHashMap"
      },
      "property": "email",
      "left": "ibney@gmail.com",
      "right": "ibneyali@gmail.com"
    },
    {
      "changeType": "ValueChange",
      "property": "phone",
      "left": "1234567",
      "right": "1234567890"
    }
  ]
}
```

## Best Practices

### 1. Use JaVers for Real-time Comparison
```java
// Good: Compare before saving
String diff = auditService.compareObjectsAsJson(oldState, newState);
if (hasSignificantChanges(diff)) {
    // Require approval
}
```

### 2. Use Custom Audit for History
```java
// Good: Query historical data from AUDIT_TABLE
List<EmployeeAuditHistoryDTO> history = 
    auditService.getEmployeeAuditHistory(employeeId);
```

### 3. Don't Mix Responsibilities
```java
// Bad: Don't try to get history from JaVers (it's not persisted)
// List<Change> changes = javers.findChanges(...); // Returns empty!

// Good: Use custom audit for history
List<AuditEvent> events = auditService.getAuditEventsByEmployeeId(employeeId);
```

## Troubleshooting

### Issue: JaVers comparison throws error "COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED"

**Cause:** Trying to compare primitive values or strings directly.

**Solution:** Parse to objects first:
```java
// ❌ Bad
javers.compare(jsonString1, jsonString2);

// ✅ Good
Map<String, Object> obj1 = objectMapper.readValue(jsonString1, Map.class);
Map<String, Object> obj2 = objectMapper.readValue(jsonString2, Map.class);
javers.compare(obj1, obj2);
```

### Issue: `findChanges()` returns empty list

**Cause:** Nothing is committed to JaVers database.

**Solution:** Use custom audit system for historical queries:
```java
// Instead of
List<Change> changes = javers.findChanges(...);

// Use
List<AuditEvent> events = auditService.getAuditEventsByEmployeeId(employeeId);
```

## Summary

### Current Implementation Capabilities

✅ **What You CAN Do:**
- Compare two objects in real-time
- Compare two JSON strings (after parsing to objects)
- Detect field-level changes
- Compare nested objects
- Generate diff reports

❌ **What You CANNOT Do:**
- Query historical changes from JaVers
- Track changes over time in JaVers
- Use JaVers findChanges() or findSnapshots()
- Query JaVers by author or date

✅ **What Custom Audit System Provides:**
- Complete historical tracking
- Query by employee, date, initiator, event type
- Field-level change tracking
- Compliance and audit trails

---

**Last Updated:** November 3, 2025  
**JaVers Version:** 7.6.2 (Core)  
**Configuration:** Object comparison only (no database persistence)

