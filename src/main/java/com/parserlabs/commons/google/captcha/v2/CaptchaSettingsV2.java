package com.parserlabs.commons.google.captcha.v2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class CaptchaSettingsV2 {
	
	private String site;
	private String secret;
	private float threshold;
	private String apikey;


}
