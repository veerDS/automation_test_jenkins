package com.demo;

public class ResponseFromPage {

	private boolean isTrue;
	private String message;

	public ResponseFromPage(boolean isTrue, String message) {
		super();
		this.isTrue = isTrue;
		this.message = message;
	}

	public boolean isTrue() {
		return isTrue;
	}


	public String getMessage() {
		return message;
	}


}
