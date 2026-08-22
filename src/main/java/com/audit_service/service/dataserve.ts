public fetchClientDetails(
  paramsData?: {
    clientName?: string;
    clientId?: string | number;
    page?: number;
    size?: number;
    sort?: string;
  }
): Observable<ApiResponse<PagedData<ClearingSystemData>>> {

  let params = new HttpParams();

  if (paramsData?.clientName?.trim()) {
    params = params.set(
      'clientName',
      paramsData.clientName.trim()
    );
  }

  if (
    paramsData?.clientId !== null &&
    paramsData?.clientId !== undefined &&
    String(paramsData.clientId).trim() !== ''
  ) {
    params = params.set(
      'clientId',
      String(paramsData.clientId)
    );
  }

  if (paramsData?.page !== null && paramsData?.page !== undefined) {
    params = params.set(
      'page',
      String(paramsData.page)
    );
  }

  if (paramsData?.size !== null && paramsData?.size !== undefined) {
    params = params.set(
      'size',
      String(paramsData.size)
    );
  }

  if (paramsData?.sort?.trim()) {
    params = params.set(
      'sort',
      paramsData.sort.trim()
    );
  }

  return this.http.get<
    ApiResponse<PagedData<ClearingSystemData>>
  >(
    `${this.baseUrl}/client-details`,
    { params }
  );
}