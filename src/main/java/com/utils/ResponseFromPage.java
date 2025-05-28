package com.utils;

public class ResponseFromPage {
	private boolean isTrue;
	private String message;
	private String extentMessage;

	public ResponseFromPage(boolean isTrue, String message) {
		super();
		this.isTrue = isTrue;
		this.message = message;
	}
	public ResponseFromPage(boolean isTrue, String message, String extentMessage) {
		super();
		this.isTrue = isTrue;
		this.message = message;
		this.extentMessage = extentMessage;
	}
	public boolean isTrue() {
		return isTrue;
	}
	public String getMessage() {
		return message;
	}
	public String getExtentMessage() {
		return extentMessage;
	}
}
