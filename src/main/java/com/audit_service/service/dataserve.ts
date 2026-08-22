fetchClientData(
  formData: any
): Observable<ApiResponse<PagedData<ClearingSystemData>>> {

  if (!formData || typeof formData !== 'object') {
    return throwError(
      () => new Error('Invalid form data provided')
    );
  }

  let params = new HttpParams();

  if (formData.clientName?.trim()) {
    params = params.set(
      'clientName',
      formData.clientName.trim()
    );
  }

  if (
    formData.clrEntId !== undefined &&
    formData.clrEntId !== null &&
    String(formData.clrEntId).trim()
  ) {
    params = params.set(
      'clrEntId',
      String(formData.clrEntId).trim()
    );
  }

  params = params.set(
    'page',
    String(formData.page ?? 0)
  );

  params = params.set(
    'size',
    String(formData.size ?? 10)
  );

  if (formData.sort) {
    params = params.set(
      'sort',
      formData.sort
    );
  }

  return this.http
    .get<ApiResponse<PagedData<ClearingSystemData>>>(
      `${this.apiUrl}/client-details`,
      { params }
    )
    .pipe(
      catchError(error => {
        console.error(
          'Error fetching client details:',
          error
        );

        return throwError(() => error);
      })
    );
}