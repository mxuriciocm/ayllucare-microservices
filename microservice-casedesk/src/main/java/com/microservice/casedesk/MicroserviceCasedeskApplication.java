package com.microservice.casedesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableJpaAuditing
@SpringBootApplication
public class MicroserviceCasedeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceCasedeskApplication.class, args);
	}

}
