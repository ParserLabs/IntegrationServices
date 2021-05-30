package com.parserlabs.commons.exception;

public class StateNotValidException extends CommonBusinessException {

	private static final long serialVersionUID = -8498255481262061602L;
	private static final String CODE = "STATE_NOT_VALID_EXCEPTION";
	private static final String DEFAULT_MSG = "Please enter valid state";

	public StateNotValidException() {
		super(CODE, DEFAULT_MSG);
	}

	public StateNotValidException(String message) {
		super(CODE, message);
	}

	public StateNotValidException(Throwable cause) {
		super(CODE, DEFAULT_MSG, cause);
	}

	public StateNotValidException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

}
