package com.microservice.casedesk.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQExplicitConfig {

    /**
     * Declarar explícitamente el exchange de triage
     */
    @Bean
    public TopicExchange triageEventsExchange() {
        log.info("🔧 Creating triageEventsExchange explicitly");
        return new TopicExchange("triage.events", true, false);
    }

    /**
     * Declarar explícitamente la cola para CaseDesk
     * El nombre debe coincidir con lo que Spring Cloud Stream espera
     */
    @Bean
    public Queue triageResultQueue() {
        String queueName = "triage.events.casedesk-service";
        log.info("🔧 Creating queue explicitly: {}", queueName);
        return QueueBuilder.durable(queueName).build();
    }

    /**
     * Binding explícito de la cola al exchange con routing key
     */
    @Bean
    public Binding triageResultBinding(Queue triageResultQueue, TopicExchange triageEventsExchange) {
        String routingKey = "triage.result.created";
        log.info("🔧 Creating binding: {} -> {} with routing key: {}",
                triageResultQueue.getName(), triageEventsExchange.getName(), routingKey);
        return BindingBuilder
                .bind(triageResultQueue)
                .to(triageEventsExchange)
                .with(routingKey);
    }

    /**
     * Declarar explícitamente el exchange de salida de CaseDesk
     */
    @Bean
    public TopicExchange casedeskEventsExchange() {
        log.info("🔧 Creating casedeskEventsExchange explicitly");
        return new TopicExchange("casedesk.events", true, false);
    }
}

