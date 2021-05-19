package com.parserlabs.commons.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnExpression("${rest.config.enabled:false}")
public class RestTemplateConfig {
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


}
