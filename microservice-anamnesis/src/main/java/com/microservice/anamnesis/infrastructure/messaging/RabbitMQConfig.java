package com.microservice.anamnesis.infrastructure.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Supplier;

/**
 * RabbitMQ configuration for anamnesis events.
 * Uses Spring Cloud Stream for event publishing.
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public Supplier<Message<?>> anamnesisSummaryCreatedSupplier() {
        return () -> null;
    }

    @Bean
    public Supplier<Message<?>> anamnesisSessionCompletedSupplier() {
        return () -> null;
    }
}


