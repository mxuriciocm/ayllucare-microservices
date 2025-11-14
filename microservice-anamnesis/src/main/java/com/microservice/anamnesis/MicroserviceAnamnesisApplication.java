package com.microservice.anamnesis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Anamnesis-LLM Microservice Application.
 *
 * This microservice orchestrates conversational anamnesis (medical interviews)
 * using Large Language Models (LLM) for the AylluCare/B4U rural digital health platform.
 *
 * Key Features:
 * - Conversational anamnesis with AI assistant
 * - Structured summary generation
 * - Integration with Profile microservice for patient context
 * - Event-driven architecture with RabbitMQ
 * - JWT-based authentication and RBAC
 *
 * @author AylluCare Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class MicroserviceAnamnesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceAnamnesisApplication.class, args);
	}

}
