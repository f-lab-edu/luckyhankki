package com.java.luckyhankki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@EnableRetry
@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class LuckyhankkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuckyhankkiApplication.class, args);
	}

}
