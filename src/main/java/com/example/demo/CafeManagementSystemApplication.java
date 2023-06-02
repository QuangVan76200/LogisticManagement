package com.example.demo;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CafeManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafeManagementSystemApplication.class, args);

	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder().group("public-api").pathsToMatch("/api/public/**").build();
	}

}
