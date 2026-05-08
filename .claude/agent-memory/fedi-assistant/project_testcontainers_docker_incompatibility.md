---
name: testcontainers vs Docker Desktop API mismatch on this machine
description: IT tests cannot run locally — project's docker-java client (testcontainers via Spring Boot 2.7.3) is too old for installed Docker Desktop 4.60+ engine API.
type: project
---

On 2026-05-07 while running InventoryService IT tests via `./mvnw verify`, every IT bootstrap fails at `PostgreSqlTestContainer` creation with:

> EnvironmentAndSystemPropertyClientProviderStrategy: failed with exception BadRequestException (Status 400: {"message":"client version 1.32 is too old. Minimum supported API version is 1.44, please upgrade your client to a newer version"})

Docker is up (Docker Desktop 4.60.1, server reachable via `npipe:////./pipe/docker_engine_linux`). The issue is project-side: testcontainers/docker-java packaged with Spring Boot 2.7.3 / jhipster-dependencies 7.9.3 only speaks Docker API ~1.32, while Docker Desktop 4.60 requires ≥1.44.

**Why:** Without a fix, IT (`*IT.java`) tests cannot be executed on this dev machine — they all error out before reaching test code with "Could not find a valid Docker environment".

**How to apply:**
- Don't waste cycles trying to make IT run in this env. Run unit tests via `./mvnw test` to validate logic; trust IT to run in CI or after the docker-java/testcontainers upgrade.
- IT classes are still worth writing — they are correct code, they just can't execute here.
- Possible fixes when revisited (in priority order): bump `testcontainers` and `docker-java` to versions that support Docker API ≥1.44 (likely testcontainers ≥1.19); or downgrade Docker Desktop; or run IT inside WSL with the WSL Docker socket. Set `~/.testcontainers.properties` `docker.host=npipe:////./pipe/docker_engine_linux` if pursuing the upgrade path.
