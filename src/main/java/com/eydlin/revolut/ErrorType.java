package com.eydlin.revolut;

import static org.glassfish.grizzly.http.util.HttpStatus.*;

public enum ErrorType {

	BALANCE_INSUFFICIENT(0, BAD_REQUEST_400.getStatusCode()),
	NO_SUCH_ACCOUNT(1, NOT_FOUND_404.getStatusCode()),
	NO_SUCH_TO_ACCOUNT(3, NOT_FOUND_404.getStatusCode()),
	ILLEGAL_AMOUNT(4, BAD_REQUEST_400.getStatusCode()),
	TO_AND_FROM_ACCOUNTS_MATCH(5, BAD_REQUEST_400.getStatusCode()),
	TO_ACCOUNT_NOT_SPECIFIED(7, BAD_REQUEST_400.getStatusCode()),
	AMOUNT_NOT_SPECIFIED(8, BAD_REQUEST_400.getStatusCode()),
	ACCOUNT_NOT_SPECIFIED(9, BAD_REQUEST_400.getStatusCode());
	
	private final int errorCode;
	private final int statusCode;
	
	ErrorType(int errorCode, int statusCode) {
		this.errorCode = errorCode;
		this.statusCode = statusCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
	
	
}
