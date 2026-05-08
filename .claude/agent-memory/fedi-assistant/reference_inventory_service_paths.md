---
name: InventoryService key paths and dev commands
description: Working directory layout, JDK location and Maven invocation patterns for the InventoryService Spring Boot/JHipster project.
type: reference
---

Working dir: `C:\Users\fedi\Documents\PFE\TS CODE\InventoryService`.

Java/Maven setup (path contains spaces — quote it):
- JAVA_HOME: `C:\Program Files\Java\jdk-17` (Java 17.0.12). The system `java.exe` on PATH (`C:\Program Files\Common Files\Oracle\Java\javapath\java.exe`) is a Microsoft Store / shim launcher with no usable JAVA_HOME — set JAVA_HOME explicitly before invoking `mvnw`.
- Bash invocation: `export JAVA_HOME="/c/Program Files/Java/jdk-17" && cd "/c/Users/fedi/Documents/PFE/TS CODE/InventoryService" && ./mvnw.cmd ...`
- Surefire (`mvn test`) runs unit tests only — IT classes (`*IT.java`) are excluded.
- Failsafe (`mvn verify`) runs IT, with `-Dspring.profiles.active=testdev` (set by the dev profile in `pom.xml`).

Layout (under `src/main/java/com/tunisales/inventory/`):
- `domain/` entities, `domain/enumeration/` enums.
- `repository/` Spring Data repos.
- `service/` services + `service/dto/` DTOs + `service/mapper/` MapStruct mappers + `service/criteria/` JHipster `*Criteria` filters + `service/util/` utility helpers (added in step 1.1).
- `web/rest/` REST resources, `web/rest/vm/` request view models, `web/rest/errors/` error VMs.
- `tenant/` TenantContext + interceptor (step 0.2).
- `client/` outbound HTTP clients (Feign-based today, e.g. `UserFeignClientInterceptor`).
- `security/` AuthoritiesConstants and security utils.

Liquibase changelog: `src/main/resources/config/liquibase/changelog/` with master `master.xml`. Existing changeset prefix pattern: `YYYYMMDDhhmmss_added_entity_<Name>.xml`.

Tests:
- `src/test/java/com/tunisales/inventory/IntegrationTest.java` is the `@IntegrationTest` composite annotation.
- `IntegrationTest` requires Docker via testcontainers (`PostgreSqlTestContainer`) when the active profile is `testdev` or `testprod`.

**How to apply:** Use these absolute paths in tool invocations. Always set JAVA_HOME before mvnw.
