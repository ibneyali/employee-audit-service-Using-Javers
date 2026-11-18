Implement the JIRA requirement for ATIS MOD Settlement Instruction generation.

Before generating a Settlement Instruction:
1. Check `ICSD_DLV_ACCT` from `TAGNTRD_SETTLEMENT`.
2. If `ICSD_DLV_ACCT` is missing or blank, check the configured Distribution Account (`CLR_ENT_DIST_AC`) in `CLR_SYS_DIST_ACC` using the correct Clearing System and Entity mapping.
3. If both accounts are unavailable, stop generation and return an appropriate validation error.
4. If either account is available, proceed according to the existing account-selection logic.

Reuse existing repository methods, entity mappings, validation patterns and exception handling. Avoid unrelated changes. Add/update unit tests for all account availability scenarios.