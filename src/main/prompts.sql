I need you to fix a bug in the Clearing System Create UI.

IMPORTANT:
- First inspect the existing implementation in:
  1. clearing-system.component.ts
  2. clearing-system-create-mapper.ts
  3. clearing-system.service.ts
  4. Any related interfaces/types used by ClientSearchItem and the client typeahead.
- Do NOT rewrite unrelated code.
- Do NOT change the backend/API contract.
- Preserve the existing behavior for clients whose names are unique.
- Make the smallest clean TypeScript/Angular change required.

BUG:
The Client Name typeahead can contain duplicate client names.

Example:

Client Name:        LCN/ADDR:
ABBY MARCOS SHINN   5019951/001
ABBY MARCOS SHINN   5011351/001

When I select the first record, LCN/ADDR and GFC ID are populated correctly.

When I then select the second record, the UI still shows the LCN/ADDR and GFC ID of the first record.

The issue happens ONLY when client names are duplicated.

ROOT CAUSE:
The current code uses clientName as the matching/cache/lookup key in some places. Since clientName is not unique, the first matching client is returned.

REQUIRED SOLUTION:
Use LCN/ADDR as the primary unique identifier for the selected client record.

The selected typeahead option already contains the rich option data, including lcnAddr. Use that value instead of relying only on clientName.

Required behavior:

1. When a typeahead option is selected, extract:
   - clientName
   - lcnAddr

2. Use lcnAddr as the primary lookup/cache/matching key.

3. Do NOT use clientName alone to identify the selected record.

4. When finding a ClientSearchItem from clientSearchOptions:
   - First match by lcnAddr.
   - Only fall back to clientName if lcnAddr is unavailable.

5. When processing the API response:
   - First find the matching record by lcnAddr.
   - Only fall back to clientName if lcnAddr is unavailable.

6. If there is any cache/map currently keyed by clientName, change the key to lcnAddr (or use a normalized lcnAddr key).

7. Preserve the existing identifier-based fallback logic such as clrEntClientId/clntRoleId if it is already required elsewhere.

8. Do not remove the existing inputFormatter, viewFields, optionFields, emitPrimitiveValue, emitSearchTerm, or allowEmptyValue behavior unless absolutely necessary.

9. Do not change the UI appearance.

10. Do not change the API request unnecessarily.

IMPORTANT IMPLEMENTATION DETAIL:

There is already logic similar to:

private resolveSelectedClientName(selectedValue: unknown): string

Add/use a corresponding helper to safely extract LCN/ADDR from the selected rich typeahead option, for example:

private resolveSelectedClientLcnAddr(selectedValue: unknown): string

It should safely handle:
- null/undefined
- string values
- object values
- wrapped object values if the shared typeahead returns an object such as { item: ... }

The helper should return an empty string when lcnAddr cannot be resolved.

Also inspect the existing:

findCreateClientBySelectedValue(...)

If it currently does something like:

const matchedByName = this.clientSearchOptions.find(
  client => client.clientName?.trim() === clientName
);

that logic must NOT be the first matching strategy because duplicate names cause the bug.

Change it so that:

1. selected lcnAddr is extracted from selectedValue.
2. clientSearchOptions is searched by lcnAddr first.
3. clientName is used only as fallback.
4. Existing identifier matching remains as a final fallback if currently present.

Also inspect the method that resolves the API response record, such as:

resolveCreateClientDetailsRow(...)

If it currently does:

rows.find(row => row.clientName?.trim() === clientName)

change the matching priority to:

1. lcnAddr
2. clientName fallback
3. existing final fallback if applicable

Also inspect any method that loads client details, such as:

loadCreateClientDetailsFromSearch(...)

Pass the selected lcnAddr through the lookup flow so the correct duplicate-name record can be resolved.

If there is a cache such as:

createClientDetailsAutoFillCache

and it currently uses clientName as the key, change it so duplicate clients do not share the same cache entry.

For example:

5019951/001 -> one cache entry
5011351/001 -> another cache entry

Do NOT assume that clientName is unique.

EXPECTED RESULT:

When selecting:

ABBY MARCOS SHINN / 5019951/001

the UI must populate the LCN/ADDR and GFC ID belonging to 5019951/001.

Then when selecting:

ABBY MARCOS SHINN / 5011351/001

the UI must update to the LCN/ADDR and GFC ID belonging to 5011351/001.

It must NOT retain the values from the first selection.

Also test the reverse order:

1. Select 5011351/001
2. Select 5019951/001

Both must update correctly.

Also test a unique client name to ensure the existing behavior still works.

Before making changes:
- Search the entire clearing-system component for all usages of clientName, createClientDetailsAutoFillCache, createClientDetailsLookupKey, findCreateClientBySelectedValue, resolveSelectedClientName, resolveSelectedOptionIdentifier, and resolveCreateClientDetailsRow.
- Identify every place where clientName is incorrectly being treated as a unique identifier.
- Then make the minimum changes required.

After making the changes:
1. Show me the exact files changed.
2. Show me the exact code changes/diff.
3. Explain why the original code returned the first duplicate client.
4. Explain why the new implementation correctly distinguishes duplicate client names using LCN/ADDR.
5. Check for TypeScript compilation/type errors.
6. Do not modify unrelated files.


Do not refactor the component. Keep the existing implementation and make only the minimal changes required to use LCN/ADDR as the unique selection/matching key for duplicate client names.