package com.parserlabs.commons.google.captcha;

public interface CaptchaService {

	boolean isCaptchaValid(final String token);
}
