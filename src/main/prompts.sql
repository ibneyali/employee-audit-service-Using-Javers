private resolveCreateClientDetailsRow(
  clientName: string,
  selectedLcnAddr = ''
): ClientSearchItem | null {

  // 1. Always try LCN/ADDR first.
  const selectedLcnAddrKey =
    this.normalizeCreateClientLcnAddrKey(selectedLcnAddr);

  if (selectedLcnAddrKey !== '') {
    const matchedByLcnAddr = this.clientSearchOptions.find(client =>
      this.normalizeCreateClientLcnAddrKey(client?.lcnAddr) ===
      selectedLcnAddrKey
    );

    if (matchedByLcnAddr) {
      return matchedByLcnAddr;
    }
  }

  // 2. If no LCN/ADDR was supplied, try the selected identifier.
  const selectedIdentifier =
    this.resolveSelectedOptionIdentifier(clientName);

  if (selectedIdentifier !== null) {
    const selectedIdentifierKey =
      this.toClientIdentifierKey(selectedIdentifier);

    const matchedByIdentifier =
      this.clientSearchOptions.find(client =>
        this.toClientIdentifierKey(
          client?.clntId ?? client?.clntRoleId
        ) === selectedIdentifierKey
      );

    if (matchedByIdentifier) {
      return matchedByIdentifier;
    }
  }

  // 3. IMPORTANT:
  // Do NOT match by clientName here because duplicate names are valid.
  return null;
}