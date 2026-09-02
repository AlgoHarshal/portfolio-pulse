package com.example.portfoliopulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PortfolioPulseApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioPulseApplication.class, args);
	}

}
