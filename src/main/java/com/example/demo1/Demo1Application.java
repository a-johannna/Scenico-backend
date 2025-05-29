package com.example.demo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Demo1Application {

	public static void main(String[] args) {
		SpringApplication.run(Demo1Application.class, args);
	}
	@Configuration
	public static class WebConfig implements WebMvcConfigurer {
		@Override
		public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
			registry.addMapping("/**")
					.allowedOrigins("https://scenico-community-production.up.railway.app/")
					.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
					.allowCredentials(true);
		}
	}
}
