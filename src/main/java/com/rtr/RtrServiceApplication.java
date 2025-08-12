package com.rtr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.rtr.client") 
public class RtrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtrServiceApplication.class, args);
	}

}
