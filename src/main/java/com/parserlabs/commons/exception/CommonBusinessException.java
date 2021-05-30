package com.parserlabs.commons.exception;

import com.parserlabs.commons.exception.model.CommonErrorAttribute;

import lombok.Getter;

public class CommonBusinessException extends RuntimeException {

	private static final long serialVersionUID = 4151569448693017560L;

	@Getter
	private CommonErrorAttribute attribute;

	@Getter
	private final String code;

	public CommonBusinessException(String code) {
		this.code = code;
	}

	public CommonBusinessException(String code, CommonErrorAttribute attribute) {
		this.code = code;
		this.attribute = attribute;
	}

	public CommonBusinessException(String code, CommonErrorAttribute attribute, Throwable cause) {
		super(cause);
		this.code = code;
		this.attribute = attribute;
	}

	public CommonBusinessException(String code, String message) {
		super(message);
		this.code = code;
	}

	public CommonBusinessException(String code, String message, CommonErrorAttribute attribute) {
		super(message);
		this.code = code;
		this.attribute = attribute;
	}

	public CommonBusinessException(String code, String message, CommonErrorAttribute attribute, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.attribute = attribute;
	}

	public CommonBusinessException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public CommonBusinessException(String code, Throwable cause) {
		super(cause);
		this.code = code;
	}
}
