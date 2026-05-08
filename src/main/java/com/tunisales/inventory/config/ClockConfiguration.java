package com.tunisales.inventory.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a single {@link Clock} bean so scheduling/auditing code can be
 * deterministic in tests (where a fixed clock is substituted).
 */
@Configuration
public class ClockConfiguration {

    @Bean
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }
}
