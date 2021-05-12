package com.parserlabs.commons.exception;

public class ReCaptchaInvalidException extends RuntimeException {

	private static final String DEFAULT_MSG = "Please verify captcha.";

	private static final long serialVersionUID = 8036873107023947041L;

	public ReCaptchaInvalidException() {
		super(DEFAULT_MSG);
	}

	public ReCaptchaInvalidException(String message) {
		super(message);
	}

	public ReCaptchaInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReCaptchaInvalidException(Throwable cause) {
		super(DEFAULT_MSG, cause);
	}

}
