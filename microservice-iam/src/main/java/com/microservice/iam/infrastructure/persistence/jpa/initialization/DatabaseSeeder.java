package com.microservice.iam.infrastructure.persistence.jpa.initialization;

import com.microservice.iam.domain.model.commands.SeedRolesCommand;
import com.microservice.iam.domain.services.RoleCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DatabaseSeeder for AylluCare/B4U platform.
 * <p>
 *     Seeds initial data into the database on application startup.
 *     Currently seeds the three roles: ROLE_PATIENT, ROLE_DOCTOR, ROLE_ADMIN.
 * </p>
 */
@Configuration
public class DatabaseSeeder {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    /**
     * Seed roles on application startup.
     *
     * @param roleCommandService the role command service
     * @return CommandLineRunner bean
     */
    @Bean
    public CommandLineRunner seedRoles(RoleCommandService roleCommandService) {
        return args -> {
            log.info("========================================");
            log.info("Seeding roles for AylluCare/B4U...");
            log.info("========================================");

            try {
                roleCommandService.handle(new SeedRolesCommand());
                log.info("✓ Roles seeded successfully:");
                log.info("  - ROLE_PATIENT");
                log.info("  - ROLE_DOCTOR");
                log.info("  - ROLE_ADMIN");
            } catch (Exception e) {
                log.error("✗ Failed to seed roles: {}", e.getMessage());
            }

            log.info("========================================");
        };
    }
}

