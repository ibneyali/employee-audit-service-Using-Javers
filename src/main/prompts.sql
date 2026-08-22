I have reverted/stashed all previous changes related to the Clearing System duplicate-client-name/autopopulation issue.

IMPORTANT:
Start from the current clean code. Do NOT try to restore, reuse, or recreate the previous cache-based solution.

I want to implement this in a simpler way, following the existing Standard Payments implementation/pattern in this application.

REQUIREMENT:

In the Clearing System -> Create New -> Client Details section:

1. Client Name is a typeahead/dropdown.
2. Duplicate client names are valid.
3. Each dropdown option must represent the correct client.
4. When the user selects a Client Name option, immediately call a NEW backend API using the selected client's `clientId`.
5. The API returns the client details required for the form, including:
   - `lcnAddr`
   - `gfcId`
   - and any other existing fields required by the current create flow.
6. After the API response is received, immediately populate:
   - LCN/ADDR field with `lcnAddr`
   - GFC ID field with `gfcId`
7. Do NOT get LCN/ADDR or GFC ID from any frontend cache.
8. Do NOT match the selected client by `clientName`, because duplicate client names are valid.
9. The selected client's `clientId` must be the identifier used to call the new API.
10. Keep the existing create payload behavior intact.

NEW API:

The API is already available:

GET

`/api/v1/clearing-systems/{clientId}/client-info`

Example response:

{
  "clientId": "537745",
  "clientName": "ABBY MARCOS SHINN",
  "lcnAddr": "5011351/001",
  "gfcId": "5011351/001"
}

Use the actual API response/model from the current project if the exact field names differ.

IMPORTANT IMPLEMENTATION GUIDANCE:

Follow the same implementation pattern already used by the Standard Payments client-name typeahead.

First search the project for the Standard Payments implementation of:

- client-name typeahead
- selection/change event
- obtaining the selected client's ID
- calling an API after client selection
- patching/populating dependent fields

Use that as the reference implementation instead of inventing a new pattern.

TRACE THE EXISTING CLEARING SYSTEM FLOW:

Please inspect these files/classes before making changes:

- clearing-system.component.ts
- clearing-system-create-mapper.ts
- clearing-system.service.ts
- clearing-system-request.model.ts
- the existing Standard Payments component/service/mapper where client-name selection and dependent-field population are already implemented.

Also inspect the existing `clientSearchOptions` / client dropdown API response to determine where `clientId` is currently available.

The dropdown option must retain the `clientId`.

For example, if the existing client object contains:

{
  clientId,
  clientName,
  clntRoleId,
  lcnAddr,
  gfcId
}

do NOT reduce the option to only:

{
  value,
  clientName,
  lcnAddr
}

Instead preserve the identifier needed for the API call.

EXPECTED FLOW:

Client Name dropdown
        ↓
User selects a client
        ↓
Get selected option/clientId
        ↓
Call NEW client-info API using clientId
        ↓
Receive lcnAddr + gfcId
        ↓
Patch Client Details form
        ↓
LCN/ADDR = response.lcnAddr
GFC ID   = response.gfcId

IMPORTANT:

Do NOT modify the common library.
Do NOT modify shared.module.ts.
Do NOT modify the common form-group/typeahead component.
Do NOT modify the common Standard Payments implementation.

Only modify the Clearing System feature files and service/model files that are required.

Do NOT introduce:
- client details cache
- resolveCreateClientDetailsRow()
- resolveSelectedOptionIdentifier()
- matching by clientName
- duplicate lookup logic
- frontend searching through clientSearchOptions after selection

The API should be the source of truth for LCN/ADDR and GFC ID.

Also ensure that the existing create payload still receives the correct client identifier (`clientId`/`clntRoleId`, whichever the backend currently expects).

Before modifying code, explain:

1. How Standard Payments handles client selection.
2. Where the selected `clientId` comes from.
3. Where the new Clearing System API should be called.
4. Which existing form patch/update mechanism should be reused.
5. Which exact files need to change.

Then implement the minimum required changes.

After implementation, verify:
- TypeScript compilation
- no unused imports
- no references to the old cache-based solution
- duplicate client names still appear in the dropdown
- selecting client A calls the API with client A's clientId
- selecting client B with the same name but a different clientId calls the API with client B's clientId
- LCN/ADDR and GFC ID are populated from the API response
- existing create payload is not broken.

Do not make unrelated refactoring.

If you need check how standard payment implemented it you can refer it.