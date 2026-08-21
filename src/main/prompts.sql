I have a bug in the Clearing System Create New screen.

Problem:
The Client Name dropdown can contain duplicate client names with different LCN/ADDR values.

Example:

1. Client Name: ABBY MARCOS SHINN
   LCN/ADDR: 5019951/001
   GFC ID: 1031796403

2. Client Name: ABBY MARCOS SHINN
   LCN/ADDR: 5011351/001
   GFC ID: <different value>

When I select the first option, LCN/ADDR and GFC ID populate correctly.

However, when I select the second option, the UI still populates the first record's LCN/ADDR and GFC ID.

Important:
Do NOT solve this by adding more fallback .find() logic based only on clientName.

The root problem appears to be that the selected dropdown/typeahead value is losing the unique LCN/ADDR and eventually the component receives only the client name string. Since both client names are identical, code such as:

this.clientSearchOptions.find(client =>
    client.clientName?.trim() === clientName
)

always returns the first record.

I need you to trace the complete selection flow and fix the actual source of the problem.

Files involved include:
- clearing-system.component.ts
- clearing-system-create-mapper.ts
- clearing-system.service.ts
- shared.module.ts
- the shared typeahead/dropdown component used by the Client Name field
- any configuration/schema/mapping that defines the Client Name dropdown options

Current code already contains methods such as:
- applyCreateClientSelection()
- resolveSelectedClientName()
- resolveSelectedClientLcnAddr()
- loadCreateClientDetailsFromSearch()
- resolveCreateClientDetailsRow()
- findCreateClientBySelectedValue()
- normalizeCreateClientLookupKey()
- normalizeCreateClientLcnAddrKey()

Do NOT simply add another lookup fallback.

Required solution:

1. Find where clientSearchOptions are converted into dropdown/typeahead options.

2. Ensure each dropdown option preserves a unique identifier.
   Prefer LCN/ADDR as the unique identifier because duplicate client names are valid.

3. The selected value emitted by the dropdown must preserve the selected option's LCN/ADDR.

4. The selection should effectively contain enough information to distinguish:

   {
       clientName: "ABBY MARCOS SHINN",
       lcnAddr: "5019951/001"
   }

   from:

   {
       clientName: "ABBY MARCOS SHINN",
       lcnAddr: "5011351/001"
   }

5. Update the Client Name dropdown/typeahead mapping so that selecting the second row emits the second row's LCN/ADDR instead of only emitting the duplicate clientName.

6. Update ClearingSystemComponent only where necessary to consume this unique selection.

7. When loading Client Details, always match by LCN/ADDR first.

8. Client name should only be used for display, NOT as the unique identifier.

9. Remove or avoid any code path where:
   
   clientSearchOptions.find(client => client.clientName === clientName)

   can incorrectly select the first duplicate record.

10. Do not change backend APIs, database queries, or unrelated functionality unless absolutely necessary.

11. Preserve existing behavior for clients whose names are unique.

12. Preserve the existing UI display:
    Client Name should still display "ABBY MARCOS SHINN",
    while LCN/ADDR and GFC ID should come from the exact selected row.

13. Check whether the shared typeahead component emits:
    - the raw string,
    - option.value,
    - the complete option object,
    - or an { item: ... } wrapper.

   Adapt the fix to the actual emitted shape instead of assuming it.

14. Also check the HTML/template/configuration for the Client Name field. The fix may need to be made where the dropdown's option value/display value is configured, not only in clearing-system.component.ts.

15. After making the change, explain exactly:
    - which file was changed
    - which method/configuration was changed
    - what the selected value looks like for the first record
    - what the selected value looks like for the second record
    - why duplicate names no longer cause the first record to be selected.

Expected behavior:

Selecting:

ABBY MARCOS SHINN | 5019951/001

must populate:

LCN/ADDR = 5019951/001
GFC ID = GFC ID belonging to 5019951/001

Selecting:

ABBY MARCOS SHINN | 5011351/001

must populate:

LCN/ADDR = 5011351/001
GFC ID = GFC ID belonging to 5011351/001

The second selection MUST NOT populate 5019951/001.

Before modifying code, inspect the actual Client Name dropdown/typeahead option mapping and selection event flow. Then make the smallest correct change at the source where the unique LCN/ADDR is being lost.