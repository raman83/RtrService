package com.rtr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import com.commons.security.DefaultSecurityConfig;
import com.commons.security.FeignTokenRelayConfig;

@SpringBootApplication
@Import({DefaultSecurityConfig.class, FeignTokenRelayConfig.class})
@EnableFeignClients(basePackages = "com.payment.client") 
public class RtrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtrServiceApplication.class, args);
	}

}
