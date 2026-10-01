I have an issue on the Settlement Instructions screen.

Please investigate and fix the dropdown population issue by comparing the Settlement Instructions screen with the Trade / Pre Issuance Trade screen.

Observed behavior:
- On the Settlement Instructions screen, the dropdowns are visible but no values/options are available.
- Examples include:
  - Security Type
  - Security ID
  - Clearing System
  - Process Mode
  - Product Type
  - SIT CCY / Settlement Currency
  - Issue Date
  - Entity Name
  - Status
  - and other dropdown fields.
- When I open the corresponding dropdowns, they are empty.
- However, on the Trade / Pre Issuance Trade screen, dropdown values are available and can be selected.
- Therefore, do NOT assume that the master data itself is missing. The same/reference data appears to be available elsewhere in the application.

Please investigate the root cause thoroughly.

### 1. Compare the working and non-working screens

Compare:
- Settlement Instructions screen
- Trade screen
- Pre Issuance Trade screen

Identify how the working screens populate their dropdowns and compare that implementation with Settlement Instructions.

Check:
- API endpoints
- API service methods
- API request parameters
- API response structure
- TypeScript interfaces/models
- dropdown option mapping
- component initialization
- useEffect/ngOnInit or equivalent lifecycle logic
- state variables
- Redux/store state if used
- shared/master-data services
- caching
- dependency injection
- error handling
- loading state
- filtering logic
- transformation/mapping logic

### 2. Check API calls

For every empty Settlement Instructions dropdown:

Trace exactly where its options should come from.

Verify:
- Is the API actually being called?
- Is it called at the correct time?
- Is the API URL correct?
- Are required parameters being passed?
- Is the API response successful?
- Does the response contain data?
- Is the response being mapped correctly?
- Is the result assigned to the correct dropdown options variable?
- Is some filtering condition removing all values?
- Is the API response property name different from what Settlement Instructions expects?

Do not hardcode dropdown values.

### 3. Compare with Trade / Pre Issuance Trade

If Trade or Pre Issuance Trade already uses an existing API/service to populate the same master data, reuse the existing implementation where appropriate instead of creating duplicate APIs.

For example, if Process Mode is populated on Pre Issuance Trade with values such as:

AUTO - AUTOMATIC
MANU - MANUAL

find exactly how those values are loaded there and determine why Settlement Instructions does not receive them.

Do the same comparison for Security Type, Product Type, Clearing System, Entity Name, Currency, Status, etc.

### 4. Check for incorrect mapping

Look for issues such as:

- response.data vs response.content
- response.items vs response.results
- incorrect property names
- incorrect interface definitions
- undefined/null response handling
- mapping the display value instead of the option value
- incorrect value/label configuration
- wrong field names
- case-sensitive property mismatch
- filtering against undefined values
- dropdown options being overwritten after API response
- options being reset to [] during form initialization
- asynchronous calls executing in the wrong order

### 5. Check dependent dropdown logic

Some dropdowns may depend on other selections.

Verify whether Settlement Instructions is incorrectly waiting for a selection that should not be required initially.

For example:
- Security Type
- Security ID
- Product Type
- Clearing System
- Process Mode
- Entity Name

Determine which dropdowns are independent and which are dependent.

Do not change the business rules. Fix only incorrect initialization/dependency logic.

### 6. Check browser/API errors

Inspect the existing frontend implementation and identify any likely:
- 400/401/403/404/500 API errors
- undefined values
- JavaScript/TypeScript errors
- failed subscriptions/promises
- incorrect observables
- failed API response parsing

Add appropriate error handling if it is missing, but do not hide the actual error.

### 7. Important requirement

Do NOT hardcode values just to make the dropdown appear populated.

The final implementation must obtain values from the same legitimate source/API/master-data mechanism used by the application.

### 8. Preserve existing functionality

Do not:
- change API contracts unnecessarily
- modify backend code unless the investigation proves it is required
- duplicate existing services
- remove existing validation
- change business rules
- affect the Trade or Pre Issuance Trade screens
- introduce unrelated refactoring

Prefer the smallest clean fix.

### 9. Testing

After fixing the issue, verify:

1. Settlement Instructions page loads.
2. Each applicable dropdown is populated.
3. Dropdown values match the values available on Trade / Pre Issuance Trade.
4. Selecting a dropdown value works correctly.
5. Dependent dropdowns still work correctly.
6. Search/Clear All functionality is not affected.
7. Search Results counts are not affected.
8. No console errors occur.
9. Existing Trade and Pre Issuance Trade dropdowns continue to work.
10. Run the relevant frontend tests/build.

### Before changing code

First provide me with:

1. The Settlement Instructions component/file.
2. The Trade/Pre Issuance Trade component/file used for comparison.
3. The API/service responsible for loading the dropdown values.
4. The exact root cause of why Settlement Instructions dropdowns are empty.
5. The specific files and lines that need to be changed.

Then implement the fix.

After implementation, give me:
- Root cause
- Files changed
- Changes made
- Why the fix works
- Test/build result

don't fix test cases