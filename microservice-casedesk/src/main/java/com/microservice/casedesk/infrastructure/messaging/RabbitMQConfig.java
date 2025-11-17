package com.microservice.casedesk.infrastructure.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.function.Consumer;

@Configuration
@EnableJpaAuditing
public class RabbitMQConfig {

    @Bean
    public Consumer<String> anamnesisSummaryCreated() {
        return message -> {};
    }

    @Bean
    public Consumer<String> triageResultCreated() {
        return message -> {};
    }
}
