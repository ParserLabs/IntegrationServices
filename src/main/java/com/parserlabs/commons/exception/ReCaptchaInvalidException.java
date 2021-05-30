package com.parserlabs.commons.exception;

public class ReCaptchaInvalidException extends CommonBusinessException {

	private static final String DEFAULT_MSG = "Please verify captcha.";
	private static final String CODE = "CAPTCHA_INVALID_EXCEPTION";

	private static final long serialVersionUID = 8036873107023947041L;

	public ReCaptchaInvalidException() {
		super(CODE, DEFAULT_MSG);
	}

	public ReCaptchaInvalidException(String message) {
		super(CODE, message);
	}

	public ReCaptchaInvalidException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

	public ReCaptchaInvalidException(Throwable cause) {
		super(CODE, DEFAULT_MSG, cause);
	}

}
