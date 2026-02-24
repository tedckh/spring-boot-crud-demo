package com.example.crudapi;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(dateTimeProviderRef = "offsetDateTimeProvider")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of("SYSTEM");
	}

	@Bean(name = "offsetDateTimeProvider")
	public DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(OffsetDateTime.now());
	}

}
