package com.parserlabs.commons.google.captcha.v1;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.parserlabs.commons.exception.ReCaptchaInvalidException;
import com.parserlabs.commons.google.captcha.AbstractCaptchaService;
import com.parserlabs.commons.proxy.CommonProxy;
import com.parserlabs.commons.utility.DateUtility;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnExpression("${captcha.service.enabled:false}")
public class CaptchaServiceImpl extends AbstractCaptchaService {

	@Autowired
	private CommonProxy<Void, CaptchaResponse> captchaProxy;

	@Override
	public boolean isCaptchaValid(String token) {

		boolean isValid = false;

		final URI verifyUri = URI
				.create(String.format(RECAPTCHA_URL_TEMPLATE, captchaSettings.getSecret(), token, getClientIP()));
		CaptchaResponse captchaResponse = captchaProxy.post(verifyUri.toString(), null, CaptchaResponse.class);
		if (Objects.isNull(captchaResponse) || !captchaResponse.isSuccess()) {
			if (Objects.nonNull(captchaResponse) && captchaResponse.hasClientError()) {
				reCaptchaAttemptService.reCaptchaFailed(getClientIP());
			}
			throw new ReCaptchaInvalidException("Captcha was not successfully validated");
		}
		Duration duration = Duration.between(DateUtility.getDateTime(captchaResponse.getChallengeTs().replace("Z", "")),
				DateUtility.getCurretnDateTime());
		reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
		if (captchaResponse.isSuccess()) {
			log.info("reCaptcha has been successfully validated {}", captchaResponse.toString());
			isValid = true;
		}
		if (duration.toMinutes() > 120) {
			isValid = false;
		}

		return isValid;
	}

}
