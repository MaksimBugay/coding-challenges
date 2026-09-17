# Department hierarchy traceability

Contract version: 1

| Requirement | Acceptance criterion | Boundary | Planned verification | Work package |
| --- | --- | --- | --- | --- |
| REQ-001 | AC-001-01 | IF-001 / API-002 | TEST-001 direct manager allowed | WP-002, WP-003 |
| REQ-001 | AC-001-02 | IF-001 / API-002 | TEST-002 parent manager allowed; TEST-003 root manager allowed across multiple levels | WP-002, WP-003 |
| REQ-001 | AC-001-03 | IF-001 / API-002 | TEST-004 unrelated manager denied | WP-002, WP-003 |
| REQ-001 | AC-001-04 | IF-001 / API-002 | TEST-005 null department and null manager denied | WP-002, WP-003 |
| REQ-001 | AC-001-05 | IF-001 / IF-002 / API-002 | TEST-006 missing ancestor denied; TEST-007 cyclic ancestry denied | WP-002, WP-003 |
| REQ-002 | AC-002-01 | IF-001 / API-001 | TEST-008 unauthorized target returns false with no mutation/save | WP-002, WP-003 |
| REQ-003 | AC-003-01 | IF-001 / API-001 | TEST-009 root target rejected with no mutation/save | WP-002, WP-003 |
| REQ-003 | AC-003-02 | IF-001 / API-001 | TEST-010 null proposed parent rejected before lookup/mutation/save | WP-002, WP-003 |
| REQ-004 | AC-004-01 | IF-001 / API-001 | TEST-011 unauthorized proposed parent rejected with no mutation/save | WP-002, WP-003 |
| REQ-005 | AC-005-01 | IF-001 / API-001 | TEST-012 self-parent rejected | WP-002, WP-003 |
| REQ-005 | AC-005-02 | IF-001 / IF-002 / API-001 | TEST-013 descendant proposed parent rejected | WP-002, WP-003 |
| REQ-005 | AC-005-03 | IF-001 / IF-002 / API-001 | TEST-014 missing or cyclic proposed-parent ancestry rejected | WP-002, WP-003 |
| REQ-006 | AC-006-01 | IF-001 / IF-002 / API-001 | TEST-015 valid cross-branch move returns true, changes only parent ID, and saves same target once | WP-002, WP-003 |
| REQ-007 | AC-007-01, AC-007-02 | IF-001 / IF-002 / API-001 | TEST-016 null request IDs and missing target/new parent return false without mutation/save | WP-002, WP-003 |
| REQ-008 | AC-008-01 | IF-001 / IF-002 | `mvn -DskipTests compile`; placeholder search after development | WP-001, WP-004 |
| REQ-008 | AC-008-02 | IF-001 / IF-002 | Review ISSUE-001 through ISSUE-010 against final diff and execution log | WP-004 |

TEST-001 through TEST-016 use literal expected booleans and explicit save/mutation observations derived from [api-contract.md](api-contract.md). The mock response table and assertion fixtures must remain separate.
