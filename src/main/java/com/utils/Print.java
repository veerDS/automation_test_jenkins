package com.utils;

import org.openqa.selenium.WebDriver;

public class Print {
	public  WebDriver driver;
	public static String log;

	protected void log(String message) {
		System.out.println(message);
	}
	protected String logExtent(String log, String message) {
		System.out.println(message);
		String logMessage = log+"<br />"+message+"<br />";
		return logMessage;
	}
	protected void capture(String name) {
		System.out.println("cannot take screenshot "+ name);
	}
}
