---
name: TuniSales project overview
description: TuniSales is a multi-service JHipster/Spring Boot platform; InventoryService is the inventory micro-service (mobile phones tracked by IMEI, multi-tenant via X-Tenant-Id).
type: project
---

TuniSales is a multi-service platform with at least: InventoryService, BusinessService, PlatformService, Gateway, and a Flutter app.

InventoryService specifics (cwd: `C:\Users\fedi\Documents\PFE\TS CODE\InventoryService`):
- Spring Boot 2.7.3 + JHipster 7.9.3, Java 17.
- Multi-tenant: `TenantContext` + `TenantInterceptor` set up in step 0.2 (commit `d8fabf2`). Tenant comes from header `X-Tenant-Id`; never accept tenantId in request body.
- Domain entities so far: StockItem (phone with 15-digit unique IMEI), Warehouse, StockMovement, StockAudit, StockAuditLine, Swap. Plus enums StockItemStatus, WarehouseType (LOCAL/SITE/SWAP/DEFECTIVE/MISSING), MovementType, AuditStatus, AuditResolution, SwapStatus.
- Inventory business roles to authorize new endpoints: ROLE_ADMIN_COMMERCIAL, ROLE_ADMIN_SYSTEME, ROLE_COMMERCIAL, ROLE_MAGASINIER. Defined in `security/AuthoritiesConstants` (added during step 1.1).

**Why:** This context shapes every endpoint and entity I touch in this repo — multi-tenancy, JHipster scaffolding conventions, and the business roles are load-bearing for every change.

**How to apply:** When adding code in this project, follow JHipster conventions (DTO + mapper + service + resource + Liquibase + IT) and always secure new endpoints with `@PreAuthorize` referencing `AuthoritiesConstants`. Filter queries by `TenantContext.get()` rather than accepting tenantId in payloads.
