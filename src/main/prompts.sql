Please fix the Client Name selection behavior in the **Clearing System Create New** screen.

### Issue

The Client Name dropdown can contain multiple records with the **same Client Name**, but they are different client records with different IDs and different LCN/ADDR and GFC ID values.

Example from the API response:

```text
Record 1
clientId: 58506
clientRoleId: 47900
clientName: "ABBY MARCOS SHINN"
lcnAddr: "5019951/001"
gfcid: "1031796403"

Record 2
clientId: 53745
clientRoleId: 47901
clientName: "ABBY MARCOS SHINN"
lcnAddr: "5011351/001"
gfcid: null
```

Both records have the same `clientName`, but they are different records.

### Current Behavior

1. Select the first `ABBY MARCOS SHINN`.
2. LCN/ADDR is correctly populated as:
   `5019951/001`
3. GFC ID is correctly populated as:
   `1031796403`

Then:

4. Select the second `ABBY MARCOS SHINN`.
5. The Client Name changes/selection happens, but the dependent fields are not correctly updated for the second record.
6. The previous LCN/ADDR and GFC ID values can remain populated.

This issue occurs specifically when there are **duplicate Client Names**.

When the Client Name is unique, the existing functionality works correctly.

### Expected Behavior

The selected Client record must always be identified using its **unique identifier**, NOT only by `clientName`.

For the example above:

#### Selecting Record 1

```text
Client Name: ABBY MARCOS SHINN
LCN/ADDR:    5019951/001
GFC ID:      1031796403
```

#### Selecting Record 2

```text
Client Name: ABBY MARCOS SHINN
LCN/ADDR:    5011351/001
GFC ID:      null/empty
```

When switching from Record 1 to Record 2, the UI must update the dependent fields according to Record 2.

### Important

Please inspect the existing Clearing System Client Name dropdown implementation.

Do NOT identify the selected record using only:

```typescript
clientName
```

because Client Name is not unique.

Use the existing unique fields from the API response, preferably the combination of:

```text
clientId
clientRoleId
```

or whatever unique value is already being used by the existing dropdown implementation.

### What to Check

Please trace the complete flow:

```text
Client Name dropdown
        ↓
selected option/value
        ↓
selection/change handler
        ↓
client lookup
        ↓
LCN/ADDR population
        ↓
GFC ID population
        ↓
Mnemonic / other dependent fields
```

Find where the selected client is being matched against the API response.

Look specifically for logic similar to:

```typescript
clients.find(client => client.clientName === selectedValue)
```

or:

```typescript
if (client.clientName === selectedClientName)
```

If such logic exists, change it to use the unique client identifier/value instead.

### Also Check Dropdown Option Value

Make sure each dropdown option has a unique internal value.

The display can remain:

```text
ABBY MARCOS SHINN 5019951/001
ABBY MARCOS SHINN 5011351/001
```

but the internal value should uniquely identify the record, for example:

```text
clientId + clientRoleId
```

Do NOT use the displayed Client Name as the unique value.

### Regression Requirements

Do not break the existing functionality for unique Client Names.

Verify all scenarios:

1. Unique Client Name → LCN/ADDR and GFC ID populate correctly.
2. Duplicate Client Name → selecting first record populates its values.
3. Duplicate Client Name → selecting second record updates to the second record's values.
4. Switching from second record back to first record works correctly.
5. If the selected record has `gfcid: null`, the previous GFC ID must be cleared instead of remaining from the previous selection.
6. If LCN/ADDR changes between duplicate records, the displayed LCN/ADDR must also change.
7. Existing client ID/client role ID submission behavior must remain unchanged.
8. Do not modify backend/API logic.
9. Do not change unrelated Clearing System functionality.

Please identify the exact place where Client Name is incorrectly being used as the unique identifier and make the smallest possible UI/component change to fix duplicate Client Name selection.
