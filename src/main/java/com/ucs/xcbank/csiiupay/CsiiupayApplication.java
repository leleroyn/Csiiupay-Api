package com.ucs.xcbank.csiiupay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


@EnableAutoConfiguration
@SpringBootApplication
public class CsiiupayApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(CsiiupayApplication.class);
		app.run(args);

	}
}
