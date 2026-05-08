---
name: 5 pre-existing JWT unit test failures in InventoryService (2026-05-07)
description: TokenProviderTest, TokenProviderSecurityMetersTests, JWTFilterTest fail on master too — they are not regressions when you see them.
type: project
---

As of commit `d8fabf2` (Étape 0.2 — multi-tenant TenantContext), `./mvnw test` reports:

- `JWTFilterTest.testJWTFilter` — NPE on `SecurityContext.getAuthentication().getName()`.
- `TokenProviderTest.testKeyIsSetFromBase64SecretWhenSecretIsEmpty`
- `TokenProviderTest.testKeyIsSetFromSecretWhenSecretIsNotEmpty`
- `TokenProviderSecurityMetersTests.testTokenExpiredCount`
- `TokenProviderSecurityMetersTests.testTokenUnsupportedCount`
- `TokenProviderSecurityMetersTests.testValidTokenShouldNotCountAnything`

Total: `Tests run: 48, Failures: 5, Errors: 1`.

**Why:** These failures predate any Étape-1 work. Verified by stashing my changes and re-running tests on `d8fabf2` — same exact 5 failures + 1 error.

**How to apply:** When reviewing post-change unit-test runs in this repo, ignore these 6 known JWT/security-meter failures. Only flag *new* failures introduced by the change. Eventually fix them outside of any feature branch (likely a JWT key/encoding mismatch from the JHipster generation defaults).
