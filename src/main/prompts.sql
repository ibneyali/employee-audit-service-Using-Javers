CRITICAL REQUIREMENT — REMOVE CLIENTNAME MATCHING COMPLETELY

The current bug exists because multiple client records can have the same clientName.

Therefore, do NOT keep clientName matching anywhere in the selection/autofill resolution path.

If the current code contains logic like:

const matchedByName = this.clientSearchOptions.find(client =>
    client.clientName?.trim() === clientName
);

REMOVE it.

Also remove any fallback logic such as:

if (matchedByName) {
    return matchedByName;
}

Do NOT first try clientId and then fall back to clientName.

Do NOT keep logic like:

const matchedClient = findByClientId(...) || findByClientName(...);

This is NOT acceptable because clientName is not unique.

The selection flow must be strictly:

Selected dropdown option
        ↓
Extract clientId from selected option
        ↓
Find exact client record in cached /client-details response using clientId
        ↓
Use that exact record's lcnAddr and gfcid
        ↓
Patch the corresponding fields

The only allowed lookup for resolving the selected client is:

client.clientId === selectedClientId

There must be no matching using:

- clientName
- lcnAddr
- array index
- first matching record
- fallback to another duplicate record

clientName can only be used for DISPLAY in the dropdown.

Example:

Display:
ABBY MARCOS SHINN - 5019951/001
ABBY MARCOS SHINN - 5011351/001

Internal selection:

First option:
clientId = 58506

Second option:
clientId = 53745

When clientId = 58506 is selected:
→ resolve only the record where client.clientId === 58506
→ LCN/ADDR = 5019951/001
→ GFC ID = 1031796403

When clientId = 53745 is selected:
→ resolve only the record where client.clientId === 53745
→ LCN/ADDR = 5011351/001
→ GFC ID = null/empty

Do not add clientId alongside the existing clientName matching.
Replace the existing clientName matching completely with clientId-based matching.

Before completing the changes, search the relevant create/autofill/selection code for:

- clientName?.trim()
- === clientName
- find(...clientName...)
- matchedByName
- findCreateClientBySelectedValue
- any fallback using clientName

Remove clientName-based matching from the client selection and autofill path.

After making changes, explicitly confirm:

"Client Name is now used only for display. Client selection and autofill are resolved exclusively using clientId."

Also ensure the final code compiles with zero TypeScript errors.