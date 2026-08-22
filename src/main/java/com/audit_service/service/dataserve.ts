private applyCreateClientSelection(
  selectedValue: unknown
): void {

  if (
    selectedValue === null ||
    selectedValue === undefined ||
    selectedValue === ''
  ) {
    return;
  }

  const selectedClientId =
    this.resolveSelectedClientId(selectedValue);

  this.selectedCreateClientId =
    selectedClientId;

  console.log(
    'Selected client ID:',
    selectedClientId
  );

  if (selectedClientId == null) {
    console.warn(
      'Unable to resolve selected client ID:',
      selectedValue
    );
    return;
  }

  const selectedClient =
    this.findCreateClientBySelectedValue(
      selectedValue
    );

  console.log(
    'Selected client:',
    selectedClient
  );

  /*
   * If the selected client is already available in the
   * cached options, we can get its name immediately.
   */
  const clientName =
    selectedClient?.clientName?.trim() ?? '';

  /*
   * Fetch the complete Client Details using CLIENT ID.
   *
   * This is the important change.
   * Do not use clientName as the primary lookup when
   * clientId is available.
   */
  this.loadCreateClientDetailsFromSearch(
    clientName,
    selectedClientId
  );
}