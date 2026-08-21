I need you to investigate and fix an issue in the Clearing System Create New flow.

Requirement:
- The Client Name typeahead dropdown can contain duplicate client names.
- Duplicate client names are valid and must NOT be removed/deduplicated.
- Each option is uniquely identified by LCN/ADDR.
- When the user selects a specific Client Name + LCN/ADDR option, the selected client's details must be correctly auto-populated:
  - LCN/ADDR
  - GFC ID
  - and the correct client identifier (clntRoleId/clientId as required by the existing create payload flow).
- The selected duplicate client must remain distinguishable by its LCN/ADDR.

Important:
- Do NOT modify the common library.
- Do NOT modify shared.module.ts.
- Do NOT modify the common form-group/typeahead component.
- Make changes only in the Clearing System feature files under:
  src/screens/clearing-system/
- Preserve the existing create payload and existing distribution-account functionality.
- Do not fix this by matching only clientName, because duplicate client names are valid.

Current implementation:

In clearing-system-create-mapper.ts, buildClientDetailsFields() accepts:

buildClientDetailsFields(
  clientNameOptionsGetter: () => any[],
  onClientSelect?: ClientSelectHandler
)

The Client Name field currently contains:

controlType: 'typeahead',
options: clientNameOptionsGetter,

and an onSelect callback similar to:

onSelect: (selectedValue: unknown) => {
  console.log('CLIENT TYPEAHEAD SELECTED VALUE:', selectedValue);
  onClientSelect?.(selectedValue);
}

In clearing-system.component.ts, renderCreatePanel() calls generateCreatePanelForms() and currently passes:

undefined // onClientSelect

This means the onSelect callback may not be connected to the component's existing auto-population flow.

The component already has existing logic/methods including:
- loadCreateClientDetailsFromSearch(...)
- resolveCreateClientDetailsRow(...)
- resolveSelectedOptionIdentifier(...)
- clientSearchOptions
- createClientDetailsAutoFillCache
- patchCreateClientDetailsValues(...)
- wireClientNameCascade()
- buildCreatePayload()/create payload logic

There is also existing logic that searches by LCN/ADDR first and then identifier. Duplicate client names must never be resolved by clientName alone.

Please do the following:

1. Trace the complete flow from:
   Client Name typeahead selection
   -> form-group/typeahead onSelect
   -> buildClientDetailsFields()
   -> generateCreatePanelForms()
   -> renderCreatePanel()
   -> existing component auto-population method
   -> patchCreateClientDetailsValues()
   -> create payload.

2. Identify exactly why selecting a Client Name currently does not trigger the expected auto-population.

3. Determine what the selectedValue actually contains at runtime based on the existing typeahead implementation and option structure.

4. Check whether the current option object:
   {
     value: lcnAddr || clientName,
     clientName,
     lcnAddr
   }
   contains enough information to identify the correct client.

5. If additional identifier fields are required, add them to the option object from the existing clientSearchOptions data, preferably without changing the common library.

6. Connect the onClientSelect callback to the existing component method instead of creating duplicate auto-population logic.

7. Ensure the selected client is resolved in this order:
   a. LCN/ADDR
   b. unique client identifier such as clntRoleId/clientId if available
   c. NEVER clientName alone when duplicate names exist.

8. Preserve the existing buildCreatePayload() behavior and ensure the correct clntRoleId is still sent in the create request.

9. Do not introduce unnecessary changes to unrelated files.

10. Before changing code, explain:
   - the root cause
   - which existing method should be reused
   - which exact file(s) need modification
   - why the change will correctly support duplicate client names.

Then provide the minimal exact code changes required, with the old code and new code clearly identified.

Also check TypeScript types and make sure there are no compile errors.