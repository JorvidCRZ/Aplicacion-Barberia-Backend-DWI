package com.sistemabarberia.fadex_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableScheduling

public class  FadexBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(FadexBackendApplication.class, args);
	}
}