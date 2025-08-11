package com.ecovery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EcoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcoveryApplication.class, args);
	}

	@Bean // RestTemplate을 Spring 빈으로 등록
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
