package com.microservice.casedesk;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableRabbit
@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication
public class MicroserviceCasedeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceCasedeskApplication.class, args);
	}

}
