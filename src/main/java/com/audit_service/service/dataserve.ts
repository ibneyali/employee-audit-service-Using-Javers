## Fix Settlement Screen – Empty Filter, Pagination and Select All Issues

### Environment
DEV

### Screen
Settlement Instructions / Settlement Search Results

### Column
Old Still Instruct Status

### Problem Observed

During DEV environment testing, the following issues were identified:

**Scenario 1: Items per page = 10**
- Total records displayed: 538,180.
- Items per page: 10.
- When clicking the Select All checkbox, the UI displays 50 selected.
- This is incorrect because only 10 records are displayed on the current page.

**Scenario 2: Items per page = 100**
- The same issue occurs when the page size is changed to 100.
- Selection and filtering behaviour is not consistent with the displayed records.

**Scenario 3: Empty filter**
- Apply the Empty filter on the Old Still Instruct Status column.
- The grid should return only records where this field is NULL or empty, according to the existing data conventions.
- The filtered total count must reflect the actual matching records.
- The grid must not display unrelated records.

### Expected Behaviour

1. When Items per Page is set to 10, the grid must display a maximum of 10 matching records.
2. When Items per Page is set to 100, the grid must display a maximum of 100 matching records.
3. Applying the Empty filter must filter records before pagination.
4. The total record count must represent the filtered result count, not the original unfiltered count.
5. If the filtered result contains fewer than 10 records, display only those matching records.
6. If the filtered result contains more than 10 records, display 10 records on the first page.
7. The same behaviour must apply when page size is 100.
8. Clicking Select All must select only the records displayed on the current page.
9. For example:
   - Page size 10 + 6 matching records = 6 selected.
   - Page size 10 + 50 matching records = maximum 10 selected on the current page.
   - Page size 100 + 50 matching records = maximum 50 selected on the current page.
   - Page size 100 + 150 matching records = maximum 100 selected on the current page.
10. Navigating to another page must not automatically select records from that page.
11. Clearing the filter must restore the original result count and pagination.
12. Changing the page size must not reset or incorrectly modify the filter conditions.

### Technical Investigation

Please investigate the complete flow in the existing DEV implementation:

**Frontend**
- Identify the Settlement Instructions grid component.
- Check the column filter implementation for Old Still Instruct Status.
- Check the Empty filter condition and its request payload.
- Inspect the Select All checkbox implementation.
- Check whether selection state is maintained across pagination.
- Verify the pageSize, currentPage, selectedRows and totalRecords state management.

**Backend**
- Verify that the Empty filter condition is correctly received.
- Check whether NULL and empty string values are handled correctly.
- Ensure filtering is applied before pagination.
- Verify that the count query and data query use identical filter conditions.
- Check whether the backend returns the correct filtered total count.

### Important Investigation

Specifically investigate why the UI shows 50 selected when only 10 records are displayed.

Identify whether this is caused by:
- Incorrect Select All implementation.
- Stale selection state.
- Selection across multiple pages.
- Incorrect API response.
- Incorrect pagination state.
- Incorrect filter processing.

Do not assume the issue is frontend-only.

### Testing Requirements

Test the following combinations:

| Page size | Filter | Expected selection |
|---|---|---|
| 10 | Empty, 6 matching records | 6 |
| 10 | Empty, 50 matching records | 10 |
| 100 | Empty, 50 matching records | 50 |
| 100 | Empty, 150 matching records | 100 |
| 10 | No filter | Maximum 10 |
| 100 | No filter | Maximum 100 |

Also verify:
- Zero matching records.
- NULL values.
- Empty strings.
- Whitespace-only values, if supported by existing data conventions.
- Pagination after filtering.
- Clearing the filter.
- Changing page size from 10 to 100 and vice versa.
- Select All and deselect All behaviour.

### Implementation Instructions

- First reproduce the issue in DEV.
- Identify the root cause before modifying code.
- Follow the existing application architecture and coding standards.
- Avoid hardcoding 10, 50 or 100 in selection logic.
- Use the configured page size and actual filtered result set.
- Do not introduce frontend-only filtering if server-side pagination is used.
- Do not modify unrelated Settlement functionality.

After implementation, provide:
1. Root cause.
2. Frontend files modified.
3. Backend files modified.
4. API request and response changes, if any.
5. Test results.
6. Confirmation that both page sizes (10 and 100) work correctly.