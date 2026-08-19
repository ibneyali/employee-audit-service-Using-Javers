Please update only the **Client Name dropdown UI in the Clearing System Create New page**.

### Reference

The **Standard Payment → Client Name** dropdown already has the required behavior.

In Standard Payment, each dropdown option displays:

**Client Name + LCN/ADDR**

For example:

* `Client` — `5035599/001`
* `Loganathan` — `5035818/001`
* `QAAutoTest Client` — `5035761/001`
* `cc auth multi role` — `5035940/001`

### Required Change

Implement the same UI display format in the **Clearing System → Create New → Client Name** dropdown.

Currently, Clearing System displays only the Client Name.

Change it so that each dropdown option displays:

**`Client Name - LCN/ADDR`**

For example:

`cc auth multi role - 5035940/001`

### Important

The existing Clearing System functionality is already working correctly.

The selected client currently returns/uses the correct internal value/ID, so **do not change the existing selection, ID mapping, API request, or backend functionality**.

Only change the **display/label of the dropdown options**.

### Implementation Instructions

1. First inspect how the **Standard Payment Client Name dropdown** is implemented.
2. Identify how Standard Payment formats the dropdown option label to show:

   * Client Name
   * LCN/ADDR
3. Inspect the existing Clearing System Client Name dropdown implementation.
4. Apply the same display/label formatting pattern to Clearing System.
5. Use the existing Clearing System API response fields, especially:

   * `clientName`
   * `lcnAddr`
   * existing client ID / role ID fields
6. Do not create a new API.
7. Do not modify the backend.
8. Do not modify the existing client selection logic.
9. Do not change how the selected client ID/value is submitted.
10. Do not affect the existing LCN/ADDR auto-population, GFC ID, Mnemonic, or other fields.

### Expected Result

Before:

`cc auth multi role`

After:

`cc auth multi role - 5035940/001`

When the user selects the option, the existing internal value/ID selection must continue working exactly as it does now.

Please inspect the Standard Payment implementation and reuse the same approach/pattern rather than creating a different implementation.

After making the change, tell me:

* Which Clearing System file/component was changed.
* Which Standard Payment component was used as the reference.
* What exact UI mapping was added/changed.
* Confirm that the selected client ID/value and existing functionality were not changed.
