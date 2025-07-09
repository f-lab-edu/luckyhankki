package com.java.luckyhankki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class LuckyhankkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuckyhankkiApplication.class, args);
	}

}
