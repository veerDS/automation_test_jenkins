package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.controls.SeleniumControls;

public class BoxAppLoginPage extends SeleniumControls {
	 WebDriver driver;
	 WebDriverWait  wait;
	 String fixedXpathExample="//div[@id='content']//a[text()='";
	 By byEmailId = By.id("login-email");
	 By byNext = By.id("login-submit");
	 By byPassword = By.id("password-login");
	 By byLogin = By.id("login-submit-password");
	 	 	 
	public BoxAppLoginPage(WebDriver driver) {
		super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);		
	}
	
	public By getByWithKey(String key) {
		By val=null;
		switch(key) {
		case "Email Id":
			val = byEmailId;
			break;
		case "Password":
			val = byPassword;
			break;
		case "Next":
			val = byNext;
			break;
		case "Login":
			val = byLogin;
			break;	
		}
		return val;		
		}
}
