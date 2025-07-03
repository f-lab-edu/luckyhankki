package com.java.luckyhankki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LuckyhankkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuckyhankkiApplication.class, args);
	}

}
