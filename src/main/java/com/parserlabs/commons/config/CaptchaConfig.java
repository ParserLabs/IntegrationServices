package com.parserlabs.commons.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
@ConditionalOnExpression("${captcha.config.enabled:false}")
@ComponentScan(basePackages = "com.parserlabs.commons")
public class CaptchaConfig {
	 @Bean
	    public ClientHttpRequestFactory clientHttpRequestFactory() {
	        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
	        factory.setConnectTimeout(3 * 1000);
	        factory.setReadTimeout(7 * 1000);
	        return factory;
	    }

}
