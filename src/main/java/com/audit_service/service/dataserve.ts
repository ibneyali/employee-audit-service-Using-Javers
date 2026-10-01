Please investigate and fix the issues visible in the Search settlement-instruction Results data table.

Issues observed:
1. The table header and table data rows are not properly aligned. There is a visible gap/spacing between the header columns and the corresponding data columns. The header and body columns should line up exactly with no unexpected blank space or horizontal offset.
2. The Search Results tab counts are showing:
   - All: 0
   - Repair: 0
   - Authorise: 0
   However, multiple records are actually displayed in the data table. The counts must reflect the actual records returned/displayed.
3. Please investigate the root cause rather than applying only a CSS workaround.

Please inspect the complete frontend flow for this screen:
- Search Results component
- DataTable/table configuration
- Column definitions
- Table header and row rendering
- Search API response mapping
- Pagination logic
- Search result state
- All/Repair/Authorise tab count calculation
- Loading/empty-state handling

For the table alignment issue:
- Verify that the number and order of header columns exactly match the number and order of data cells.
- Check whether any column is missing from either the header or row.
- Check fixed widths, percentage widths, min-width/max-width, flex properties, padding and margins.
- Check whether horizontal scrolling or a separate header/body container is causing the misalignment.
- Ensure the header and body use the same column-width configuration.
- Do not simply hide the gap with arbitrary margins or positioning.
- Preserve the existing responsive behavior.

For the count issue:
- Trace where All, Repair and Authorise counts are calculated.
- Compare the count source with the actual API response and rendered records.
- Ensure counts are updated whenever a new search is performed.
- Ensure counts are not initialized to 0 and left unchanged because of incorrect state mapping.
- Verify the status/type field used for Repair and Authorise filtering.
- If pagination is used, determine whether the UI should display total matching records or only the current page records based on the existing application behavior.
- Do not hardcode the counts.
- Make sure the counts remain correct after search, clear search, filter changes, pagination and tab changes.

Important:
- First identify the root cause and explain it.
- Make the smallest required code changes.
- Do not modify unrelated functionality.
- Do not change API contracts unless absolutely necessary.
- Reuse the existing project patterns and components.
- Check for TypeScript/JavaScript errors after the change.
- Add or update unit/component tests for:
  1. Correct table header/data alignment.
  2. Correct All count.
  3. Correct Repair count.
  4. Correct Authorise count.
  5. Search returning zero records.
  6. Search returning multiple records.
  7. Clearing the search and restoring the correct counts.

Before making changes, show me:
1. Which files are responsible for the table.
2. Where the table columns are defined.
3. Where the Search Results counts are calculated.
4. The root cause of each issue.

Then implement the fix and provide a concise summary of the changes.

don't fix test cases