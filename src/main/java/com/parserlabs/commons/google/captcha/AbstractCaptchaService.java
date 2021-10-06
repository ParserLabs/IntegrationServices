package com.parserlabs.commons.google.captcha;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.parserlabs.commons.exception.ReCaptchaInvalidException;
import com.parserlabs.commons.google.captcha.v1.CaptchaService;
import com.parserlabs.commons.google.captcha.v1.ReCaptchaAttemptService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCaptchaService implements CaptchaService {
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected CaptchaSettings captchaSettings;
	@Autowired
	protected ReCaptchaAttemptService reCaptchaAttemptService;
	@Autowired
	protected RestTemplate restTemplate;

	protected static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

	@Value("${google.recaptcha.url_template}")
	protected String RECAPTCHA_URL_TEMPLATE;
	
	@Value("${google.recaptcha.url_template_v2}")
	protected String RECAPTCHA_URL_TEMPLATE_V2;

	protected void securityCheck(final String response) {
		log.info("Attempting to validate response {}", response);

		if (reCaptchaAttemptService.isBlocked(getClientIP())) {
			throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
		}

		if (!responseSanityCheck(response)) {
			throw new ReCaptchaInvalidException("Response contains invalid characters");
		}
	}

	protected boolean responseSanityCheck(final String response) {
		return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
	}

	protected String getClientIP() {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}

}
