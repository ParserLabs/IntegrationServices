package com.parserlabs.commons.google.captchav3;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.parserlabs.commons.google.captcha.AbstractCaptchaService;
import com.parserlabs.commons.proxy.CommonProxy;
import com.parserlabs.commons.utility.GeneralUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnExpression("${captcha.service.v3.enabled:false}")
public class CaptchaServiceV3Impl extends AbstractCaptchaService {

	

	private CommonProxy<CaptchaRequest, CaptchaEnterpriseResponse> captchaProxy;
	
	@Autowired
	private HttpServletRequest request;

	@Override
	public boolean isCaptchaValid(String token) {
		boolean captchaResult = false;
		//client ip
		String client_ip = GeneralUtils.getClientIP(request);
		
		final URI verifyUri = URI.create(String.format(RECAPTCHA_URL_TEMPLATE_V3, captchaSettings.getApikey()));
		
		//Captcha request
		CaptchaRequest captchaRequest = CaptchaRequest.builder().event(Event.of(token, captchaSettings.getSite(), ""))
				.build();
		
		//Captcha response
		CaptchaEnterpriseResponse captchaResponse = captchaProxy.post(verifyUri.toString(), captchaRequest,
				CaptchaEnterpriseResponse.class);
		if (captchaResponse.getTokenProperties().isValid() && captchaResponse.getScore() > 0.6) {
			log.info("Captcha was not successfully validated");
			captchaResult = true;
			//throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
		}else {
			log.warn("Captcha validation failed with reason {}", 
					captchaResponse.getTokenProperties().getInvalidReason());
			captchaResult = false;

		}
		// NOTE: added for checking security violations. 
		//Later it should be added in main captcha validation logic above.
		if(captchaResult==true 
				&& !captchaResponse.getEvent().getUserIpAddress().equals(client_ip)) {
			log.warn("Security Alert :: Non IP matching captcha request. "
					+ "captcha IP: {} and actual IP: {}",
					captchaResponse.getEvent().getUserIpAddress(),
					client_ip);
		}
		log.info(String.format("reCaptcha validation success statuas is {}", captchaResult));
		return  captchaResult;
	}

}
