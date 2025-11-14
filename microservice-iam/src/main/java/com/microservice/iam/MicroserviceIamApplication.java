package com.microservice.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableDiscoveryClient
@EnableJpaAuditing
@SpringBootApplication
public class MicroserviceIamApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceIamApplication.class, args);
	}

}
