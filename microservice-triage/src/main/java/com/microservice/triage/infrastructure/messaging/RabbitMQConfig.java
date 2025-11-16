package com.microservice.triage.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Triage microservice.
 */
@Configuration
public class RabbitMQConfig {

    public static final String TRIAGE_EXCHANGE = "triage.events";
    public static final String TRIAGE_RESULT_CREATED_ROUTING_KEY = "triage.result.created";

    /**
     * Topic exchange for triage events.
     */
    @Bean
    public TopicExchange triageExchange() {
        return new TopicExchange(TRIAGE_EXCHANGE);
    }

    /**
     * JSON message converter for RabbitMQ.
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate configured with JSON converter.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}

