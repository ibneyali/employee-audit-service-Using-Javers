I’m working on the Clearing System.

I have a Client Name dropdown where duplicate Client Names can exist.

Example:

ABBY MARCOS SHINN    5019951/001
ABBY MARCOS SHINN    5011351/001

These are two different client records with the same clientName, but different clientId, clientRoleId, lcnAddr, and gfcid.

API Response
Record 1:
clientId     = 58506
clientRoleId = 47900
clientName   = ABBY MARCOS SHINN
lcnAddr      = 5019951/001
gfcid        = 1031796403


Record 2:
clientId     = 53745
clientRoleId = 47901
clientName   = ABBY MARCOS SHINN
lcnAddr      = 5011351/001
gfcid        = null
Current Problem
When I select the first record, LCN/ADDR is correctly populated as 5019951/001 and GFC ID as 1031796403.
When I select the second record, the UI still populates the first record's LCN/ADDR and GFC ID.
This issue occurs only when multiple records have the same clientName.
When Client Names are different, the functionality works correctly.
The API response is correct.
The issue appears to be in the frontend selection/matching logic.
Required Change

Currently, the selection or auto-population logic may be identifying the selected record using clientName, which is not unique.

Please update the existing implementation so that:

The dropdown can continue displaying clientName to the user.
The selected record must not be identified using clientName.
Use clientRoleId as the unique identifier for selection, because clientRoleId should not be duplicated.
When a user selects a client, use the selected clientRoleId to find the exact client record.
Based on the matched record, correctly populate:
LCN/ADDR
GFC ID
For the above example:

Selecting clientRoleId = 47900 should populate:

LCN/ADDR = 5019951/001
GFC ID = 1031796403

Selecting clientRoleId = 47901 should populate:

LCN/ADDR = 5011351/001
GFC ID = null/empty
Do not change the backend API.
Do not change unrelated functionality.
Analyze my existing HTML/template and TypeScript code first, then provide the minimal required code changes.

I will provide the relevant component HTML and TypeScript files. Please identify where clientName is currently being used for selection or matching and replace that logic with clientRoleId so duplicate Client Names work correctly.