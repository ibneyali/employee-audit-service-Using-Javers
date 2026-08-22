private loadCreateClientDetailsFromSearch(
  clientName: string,
  selectedClientId: string | number
): void {

  const lookupKey =
    this.normalizeCreateClientLookupKey(
      String(selectedClientId)
    );

  this.createClientDetailsLookupSubscription?.unsubscribe();

  this.createClientDetailsLookupKey = lookupKey;

  /*
   * IMPORTANT:
   * When a client is selected, use clientId as the primary lookup.
   *
   * Do NOT use clientName when clientId is available because
   * clientName is not unique.
   */
  const clientId =
    selectedClientId !== null &&
    selectedClientId !== undefined &&
    String(selectedClientId).trim() !== ''
      ? selectedClientId
      : null;

  const requestParams: {
    clientName?: string;
    clientId?: string | number;
    page: number;
    size: number;
  } = {
    page: 0,
    size: 20
  };

  /*
   * Prefer clientId.
   * Only use clientName when clientId is unavailable.
   */
  if (clientId !== null) {
    requestParams.clientId = clientId;
  } else if (clientName?.trim()) {
    requestParams.clientName = clientName.trim();
  }

  console.log(
    'Fetching client details with params:',
    requestParams
  );

  const lookupSubscription =
    this.clearingSystemService
      .fetchClientDetails(requestParams)
      .subscribe({

        next: (
          response:
            ApiResponse<PagedData<ClearingSystemData>>
        ) => {

          const rows =
            response?.data?.content ?? [];

          console.log(
            'Client details API rows:',
            rows
          );

          console.log(
            'Client details row count:',
            rows.length
          );

          console.log(
            'Selected client ID:',
            selectedClientId
          );

          /*
           * If clientId was used, find the exact matching
           * client in the response.
           */
          let selectedClient: ClearingSystemData | null = null;

          if (clientId !== null) {

            const selectedIdKey =
              String(clientId);

            selectedClient =
              rows.find(row =>
                String(
                  row.clrEntClientId ??
                  row.clientId ??
                  ''
                ) === selectedIdKey
              ) ?? null;

          } else {

            /*
             * No clientId available.
             * Since the API was called using clientName,
             * use the first matching result.
             */
            selectedClient =
              rows[0] ?? null;
          }

          console.log(
            'Matched client details row:',
            selectedClient
          );

          if (!selectedClient) {

            console.warn(
              'No client details found for selected client:',
              {
                clientId,
                clientName
              }
            );

            return;
          }

          const resolvedClientName =
            selectedClient.clientName?.trim()
              ?? clientName?.trim()
              ?? '';

          const lcnAddr =
            selectedClient.lcnAddr?.trim() ?? '';

          const gfcid =
            selectedClient.gfcid != null
              ? String(selectedClient.gfcid)
              : '';

          console.log(
            'Updating Client Name:',
            resolvedClientName
          );

          console.log(
            'Updating LCN/ADDR:',
            lcnAddr
          );

          console.log(
            'Updating GFC ID:',
            gfcid
          );

          /*
           * Update the Create Client Details form.
           */
          this.patchCreateClientDetailsValues({
            clientName: resolvedClientName,
            lcnAddr,
            gfcid
          });

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error fetching client details:',
            error
          );
        }
      });

  this.createClientDetailsLookupSubscription =
    lookupSubscription;
}