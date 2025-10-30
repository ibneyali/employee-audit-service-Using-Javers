# JaVers Commit Flow Explained

## What Happens When You Call `javers.commit(saved.getUpdatedBy(), saved)`?

### Step-by-Step Process:

```java
javers.commit("admin", savedEmployee);
```

### 1. **JaVers Creates a Commit Record** (in `jv_commit` table)
```sql
INSERT INTO jv_commit (commit_pk, author, commit_date, commit_id)
VALUES (1, 'admin', '2025-10-30 11:53:15', 1.00);
```

### 2. **JaVers Creates a Global ID** (in `jv_global_id` table)
```sql
INSERT INTO jv_global_id (global_id_pk, type_name, local_id)
VALUES (1, 'Employee', '6');
```

### 3. **JaVers Creates a Snapshot** (in `jv_snapshot` table)
```sql
INSERT INTO jv_snapshot (snapshot_pk, type, state, global_id_fk, commit_fk)
VALUES (1, 'INITIAL', '{"firstName":"Ibney","lastName":"Ali",...}', 1, 1);
```

The `state` column contains the **entire object serialized to JSON** using Jackson!

### 4. **On Next Update, JaVers Compares**
When you update the employee again:
```java
// First update
Employee emp = {firstName: "Ibney", lastName: "Ali"}
javers.commit("admin", emp);

// Second update  
emp.setLastName("Ali Updated");
javers.commit("admin", emp);  // ← JaVers compares with previous snapshot
```

JaVers automatically:
- Compares new snapshot with previous snapshot
- Detects changes: `lastName: "Ali" → "Ali Updated"`
- Stores only the **changed properties** in `changed_properties` column
- Creates a new snapshot

---

## 🎯 Complete Flow Example

### Create Employee
```java
Employee emp = new Employee();
emp.setFirstName("Ibney");
emp.setLastName("Ali");
emp.setEmail("ibneyalimbd@gmail.com");

employeeRepository.save(emp);  // ← Saves to database
javers.commit("admin", emp);   // ← Saves to JaVers audit
```

**JaVers stores:**
```json
{
  "commitId": 1.00,
  "author": "admin",
  "snapshot": {
    "firstName": "Ibney",
    "lastName": "Ali",
    "email": "ibneyalimbd@gmail.com"
  }
}
```

### Update Employee
```java
emp.setLastName("Ali Updated");
emp.setDepartmentId(2);

employeeRepository.save(emp);  // ← Updates database
javers.commit("admin", emp);   // ← JaVers detects changes
```

**JaVers stores the DIFF:**
```json
{
  "commitId": 2.00,
  "author": "admin",
  "changes": [
    {
      "property": "lastName",
      "oldValue": "Ali",
      "newValue": "Ali Updated"
    },
    {
      "property": "departmentId",
      "oldValue": 1,
      "newValue": 2
    }
  ]
}
```

### Query Changes (Get JSON Diff)
```java
String jsonDiff = javers.getJsonConverter().toJson(
    javers.findChanges(QueryBuilder.byInstanceId(6, Employee.class).build())
);
```

**Returns:**
```json
[
  {
    "changeType": "ValueChange",
    "property": "lastName",
    "left": "Ali",
    "right": "Ali Updated",
    "commitMetadata": {
      "author": "admin",
      "commitDate": "2025-10-30T..."
    }
  }
]
```

---

## 📝 Summary

| Aspect | Explanation |
|--------|-------------|
| **`javers.commit()`** | **INPUT** - Saves entity snapshot to audit database |
| **`javers.getJsonConverter().toJson()`** | **OUTPUT** - Converts changes to JSON format |
| **First parameter** | **WHO** - Author who made the change |
| **Second parameter** | **WHAT** - The entity object to audit |
| **JSON Diff** | The comparison result returned as JSON |
| **Jackson** | Used internally by JaVers to serialize/deserialize objects |

---

## 🔄 The Complete Cycle

```
1. User updates employee
   ↓
2. EmployeeService.updateEmployee() called
   ↓
3. employeeRepository.save(emp) - saves to DB
   ↓
4. javers.commit("admin", emp) - saves to JaVers
   ↓
5. JaVers compares with previous snapshot
   ↓
6. JaVers stores the diff in jv_snapshot table
   ↓
7. Later: User queries changes
   ↓
8. javers.findChanges() - retrieves changes
   ↓
9. javers.getJsonConverter().toJson() - converts to JSON
   ↓
10. Returns JSON diff to user
```

---

## ✅ Key Takeaways

1. **`javers.commit()`** = Save snapshot for auditing
2. **`getUpdatedBy()`** = Track WHO made the change
3. **JSON Diff** = The output format when querying changes
4. **Jackson** = Used by JaVers to serialize/deserialize objects to/from JSON
5. **Automatic comparison** = JaVers compares snapshots automatically

You don't need to manually calculate diffs - JaVers does it for you!

