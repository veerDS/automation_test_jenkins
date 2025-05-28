package com.utils;

import org.openqa.selenium.WebDriver;

public class LimitBrowser {
	public String testName;
	public WebDriver driver;
	public LimitBrowser(String testName, WebDriver driver) {
		super();
		this.testName = testName;
		this.driver = driver;
	}
	public String getTestName() {
		return testName;
	}
	public WebDriver getDriver() {
		return driver;
	}
}
