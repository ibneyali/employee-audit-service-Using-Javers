Analyze and fix the filtering and pagination issue in the Settlement screen.

**Issue Description:**

In the Settlement screen, the `Old Still Instruct Status` column has a filter option called `Empty`.

Currently, the grid displays 1,359,697 total records with 100 records per page.

When selecting the `Empty` filter and applying it, the grid should display only records where `Old Still Instruct Status` is NULL, empty, or blank, depending on the existing field-handling requirements.

**Expected Behavior:**

1. When the user selects `Empty` from the `Old Still Instruct Status` column filter, only matching records should be displayed.
2. The total record count must be recalculated based on the filtered results.
3. Pagination must reflect the filtered record count.
4. If fewer than 100 records match the filter, display only those matching records.
5. If more than 100 records match, display 100 records per page.
6. The page size must remain 100 unless the user explicitly changes it.
7. The Select All checkbox must select only the records displayed on the current page (100 records), not all 1,359,697 records.
8. Changing or clearing the filter must refresh the grid data, total count, and pagination correctly.
9. Ensure that the Empty filter works correctly for NULL, empty strings, and whitespace-only values according to the application's existing data conventions.

**Technical Investigation:**

- Identify the Settlement screen component and its grid implementation.
- Trace the column filtering logic from React UI to backend API and database query.
- Verify whether the Empty filter condition is correctly passed to the backend.
- Check whether the backend query correctly handles NULL and empty values.
- Ensure filtering is performed before pagination.
- Ensure the total count query uses exactly the same filter conditions as the data query.
- Check whether pagination or Select All state is incorrectly overriding the applied filter.
- Follow existing project architecture, coding standards, API contracts, and reusable components.

**Important:**
Do not implement a frontend-only filtering workaround if the grid uses server-side pagination.

First identify the root cause, then implement the appropriate fix in the frontend and/or backend.

Do not modify unrelated functionality.

Add or update unit and integration tests covering:
- Empty filter with matching records.
- Empty filter with zero matching records.
- Empty filter with fewer than 100 records.
- Empty filter with more than 100 records.
- Pagination after applying the filter.
- Clearing the filter.
- Select All behavior after filtering.

Finally, provide:
1. Root cause of the issue.
2. Files/classes modified.
3. Code changes implemented.
4. Tests executed and their results.

please fix using minimul changes