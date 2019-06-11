package com.eydlin.revolut;

@SuppressWarnings("serial")
public class ApplicationException extends Exception {

	private final ErrorType errorType;
	
	public ApplicationException(ErrorType errorType, String message) {
		super(message);
		this.errorType = errorType;
	}

	public ErrorType getErrorType() {
		return errorType;
	}
	
}
