package com.vahabvahabov.PaySnap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class PaySnapApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaySnapApplication.class, args);
	}

}
