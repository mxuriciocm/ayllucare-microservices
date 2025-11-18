package com.microservice.casedesk;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableRabbit
@EnableJpaAuditing
@SpringBootApplication
public class MicroserviceCasedeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceCasedeskApplication.class, args);
	}

}
