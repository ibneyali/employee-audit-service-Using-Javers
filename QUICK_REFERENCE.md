# Quick Reference: JaVers Audit Endpoints

## Standard Audit Endpoints

### Get All Audit Events
```http
GET http://localhost:8080/api/audit
```

### Get Audit Events for Specific Employee
```http
GET http://localhost:8080/api/audit/employee/1
```

### Get Audit Events by Entity Type
```http
GET http://localhost:8080/api/audit/entity/EMPLOYEE
```

### Get Audit Events by Event Name
```http
GET http://localhost:8080/api/audit/event/UPDATED
```

### Get Audit Events by Initiator
```http
GET http://localhost:8080/api/audit/initiator/admin
```

### Get Employee Audit History (Formatted)
```http
GET http://localhost:8080/api/audit/employee/1/history
```

### Get Field Change History
```http
GET http://localhost:8080/api/audit/employee/1/field/email
```

---

## JaVers-Specific Endpoints (JSON Diff)

### Get Employee Changes (JaVers JSON Format)
```http
GET http://localhost:8080/api/audit/javers/employee/1
```

**Response Example:**
```json
[
  {
    "changeType": "ValueChange",
    "globalId": {
      "entity": "Employee",
      "cdoId": 1
    },
    "commitMetadata": {
      "author": "admin",
      "commitDate": "2025-10-29T10:30:00.123",
      "id": 1.00
    },
    "property": "lastName",
    "propertyChangeType": "PROPERTY_VALUE_CHANGED",
    "left": "Doe",
    "right": "Smith"
  }
]
```

### Get Employee Snapshots Over Time
```http
GET http://localhost:8080/api/audit/javers/employee/1/snapshots
```

**Response:** Full state snapshots at each commit point

### Get All Changes by Author
```http
GET http://localhost:8080/api/audit/javers/author/admin
```

**Response:** All changes made by specific author across all entities

### Get All Employee Changes
```http
GET http://localhost:8080/api/audit/javers/employees/all
```

**Response:** All changes to all employees

---

## CRUD Operations (Auto-tracked by JaVers)

### Create Employee
```http
POST http://localhost:8080/api/employees
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "departmentId": 1,
  "updatedBy": "admin"
}
```

### Update Employee
```http
PUT http://localhost:8080/api/employees/1
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "phone": "1234567890",
  "departmentId": 2,
  "updatedBy": "admin"
}
```

### Delete Employee
```http
DELETE http://localhost:8080/api/employees/1
```

All create, update, and delete operations are automatically tracked by JaVers!

---

## Testing the Integration

1. **Start the application:**
   ```bash
   gradlew bootRun
   ```

2. **Open Swagger UI:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Test workflow:**
   - Create an employee
   - Update the employee (change firstName, lastName, email)
   - Get changes using JaVers: `/api/audit/javers/employee/{id}`
   - View snapshots: `/api/audit/javers/employee/{id}/snapshots`

---

## Change Types in JaVers

- **ValueChange** - Simple property value changed
- **ReferenceChange** - Reference to another entity changed
- **ListChange** - Collection changed
- **NewObject** - Object created
- **ObjectRemoved** - Object deleted

---

## Commit Metadata

Every change includes:
- `author` - Who made the change (from updatedBy field)
- `commitDate` - When the change was made
- `id` - Unique commit identifier
- `properties` - Additional metadata (optional)

---

## Query Parameters

### Date Range Query
```http
GET http://localhost:8080/api/audit/date-range?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
```

### Entity and ID Query
```http
GET http://localhost:8080/api/audit/entity/EMPLOYEE/id/1
```

---

## Supported Entities

All entities have JaVers tracking:
- ✅ Employee
- ✅ Department
- ✅ Address
- ✅ Training
- ✅ EmployeeTraining

Simply use the same endpoints pattern for other entities!

