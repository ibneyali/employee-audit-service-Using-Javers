Please fix the Distribution Account Detail row deletion behavior in the Clearing System Create New page.

### Current Behavior

* When the Create New Clearing System page is opened, one Distribution Account Detail row is available by default.
* The user can click **Add Row** to add additional Distribution Account Detail rows.
* The user can currently delete the default/last remaining row using the **Delete** button.
* After deleting the only row, the user can submit the Clearing System without any Distribution Account Detail.
* The backend then returns an error because at least one Distribution Account Detail is mandatory.
* This should be prevented at the UI level.

### Required Behavior

1. There must always be **at least one Distribution Account Detail row**.
2. When there is exactly **1 row**:

   * The **Delete** button must be disabled.
   * The user must not be able to delete the last remaining row.
3. When there are **2 or more rows**:

   * The **Delete** button should be enabled.
   * The user should be able to select and delete rows normally.
4. The **Add Row** button should continue working normally.
5. After deleting a row:

   * If only 1 row remains, the **Delete** button must automatically become disabled.
   * If more than 1 row remains, the **Delete** button must remain enabled.
6. Do not change the existing validation, API contracts, backend logic, or other Clearing System functionality unless required for this UI fix.
7. Please reuse the existing row state/selection logic and follow the current project's coding patterns.

### Important

Please first inspect the existing Clearing System component and identify:

* Where Distribution Account Detail rows are initialized.
* How Add Row is implemented.
* How Delete is implemented.
* How the Delete button's disabled/enabled state is currently controlled.
* How selected rows are tracked.

Then implement the smallest possible change so that the Delete button is disabled whenever only one Distribution Account Detail row exists.

Also make sure the solution handles these scenarios correctly:

* **1 row → Delete disabled**
* **1 row → Add Row → 2 rows → Delete enabled**
* **2 rows → Delete one → 1 row → Delete disabled**
* **2+ rows → Delete selected row(s) → remaining rows are handled correctly**
* The user should never be able to reach **0 Distribution Account Detail rows** through the UI.

Please provide the exact files changed and briefly explain the changes after implementation.
