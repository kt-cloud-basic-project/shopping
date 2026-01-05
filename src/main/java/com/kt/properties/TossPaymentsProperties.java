package com.kt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "toss.payments")
public class TossPaymentsProperties {
		private String clientKey;
		private String secretKey;
		private String apiUrl;

		@PostConstruct
		public void init() {
			System.out.println("TossPaymentsProperties initialized with clientKey: " + clientKey);
			System.out.println("apiUrl: " + apiUrl);
			System.out.println("secretKey: " + secretKey);
		}
}
