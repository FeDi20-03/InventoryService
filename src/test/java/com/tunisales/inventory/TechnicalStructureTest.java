package com.tunisales.inventory;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = InventoryServiceApp.class, importOptions = DoNotIncludeTests.class)
class TechnicalStructureTest {

    // prettier-ignore
    @ArchTest
    static final ArchRule respectsTechnicalArchitectureLayers = layeredArchitecture()
        .layer("Config").definedBy("..config..")
        .layer("Client").definedBy("..client..")
        .layer("Web").definedBy("..web..")
        .optionalLayer("Service").definedBy("..service..")
        .layer("Security").definedBy("..security..")
        .layer("Persistence").definedBy("..repository..")
        .layer("Domain").definedBy("..domain..")

        .whereLayer("Config").mayNotBeAccessedByAnyLayer()
        // Outbound HTTP clients (PlatformNotificationClient, GpfReworkClient,
        // FeignClient interceptors, ...) live in the Client layer and are
        // injected into Service/Web layers as collaborators.
        .whereLayer("Client").mayOnlyBeAccessedByLayers("Service", "Web", "Config")
        .whereLayer("Web").mayOnlyBeAccessedByLayers("Config")
        // Client depends on service.dto (NotificationPayloadDTO etc.) so we
        // allow Service to be reached from Client as well.
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Web", "Config", "Client")
        .whereLayer("Security").mayOnlyBeAccessedByLayers("Config", "Client", "Service", "Web")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service", "Security", "Web", "Config")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Persistence", "Service", "Security", "Web", "Config")

        .ignoreDependency(belongToAnyOf(InventoryServiceApp.class), alwaysTrue())
        .ignoreDependency(alwaysTrue(), belongToAnyOf(
            com.tunisales.inventory.config.Constants.class,
            com.tunisales.inventory.config.ApplicationProperties.class
        ));
}
