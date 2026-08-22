I am working on the Clearing System and need help fixing the Client Name dropdown auto-population issue.

## Problem

The Client Name dropdown can contain duplicate client names.

Example:

ABBY MARCOS SHINN
ABBY MARCOS SHINN

These are different client records with different unique clientId values and different LCN/ADDR and GFC ID values.

When the Create New screen is opened, the existing `/client-details` API is called and the response is cached in the frontend.

Example API response:

Record 1:
clientId = 58506
clientRoleId = 47900
clientName = ABBY MARCOS SHINN
lcnAddr = 5019951/001
gfcid = 1031796403

Record 2:
clientId = 53745
clientRoleId = 47901
clientName = ABBY MARCOS SHINN
lcnAddr = 5011351/001
gfcid = null

## Current Issue

When I select Record 1, the fields are populated correctly:

LCN/ADDR = 5019951/001
GFC ID = 1031796403

However, when I select Record 2, the UI still populates the values from Record 1.

This happens because both records have the same `clientName`.

The API response itself is correct. The problem appears to be in the frontend selection or matching logic.

## Required Fix

Please analyze the existing HTML/template and TypeScript code and identify where the selected client is being matched or identified using:

- clientName
- display name
- option label
- any other non-unique value

Do not use `clientName` to identify the selected record.

The dropdown should still display `clientName` to the user, but internally the selected option must retain and use the unique `clientId`.

Use `clientId` as the unique identifier to find the exact record from the cached `/client-details` API response.

The expected logic should be similar to:

1. User selects a Client Name from the dropdown.
2. Get the unique `clientId` of the selected option/object.
3. Find the exact client record in the cached `/client-details` response using `clientId`.
4. Populate:
   - `lcnAddr`
   - `gfcid`
5. If `gfcid` is null, populate it as empty/null according to the existing form behavior.

Expected result:

Selecting clientId = 58506:
LCN/ADDR = 5019951/001
GFC ID = 1031796403

Selecting clientId = 53745:
LCN/ADDR = 5011351/001
GFC ID = null/empty

## Important Constraints

- Do not change the backend API.
- Do not change the `/client-details` API response.
- Do not make unnecessary changes to unrelated functionality.
- Do not change the dropdown display behavior; it should continue showing `clientName`.
- Reuse the existing cached `/client-details` API response.
- Do not create a new API unless the existing implementation absolutely requires it.
- Make the minimal required changes only.
- Ensure the solution works for both unique and duplicate client names.

First, analyze my existing HTML/template and TypeScript implementation.

Then clearly show:

1. Where the incorrect matching using `clientName` is happening.
2. What needs to be changed.
3. The exact updated HTML code, if required.
4. The exact updated TypeScript code.
5. Why the updated logic correctly handles duplicate client names.

Please do not provide a completely rewritten component. Make only the minimal changes required to fix this issue.

I will provide the relevant HTML and TypeScript files below.