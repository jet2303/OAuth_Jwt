package com.example.jwt_oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JwtOauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtOauthApplication.class, args);
	}

}
