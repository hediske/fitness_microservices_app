package com.fitness.hediske;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableJpaAuditing
@EnableEurekaClient
public class HediskeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HediskeApplication.class, args);
	}

}
