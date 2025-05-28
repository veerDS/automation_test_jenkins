package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.controls.SeleniumControls;

public class UnicornHomePage extends SeleniumControls {
	 WebDriver driver;
	 By bySignup = By.xpath("//button[text()=' SIGN UP NOW']");	 
	public UnicornHomePage(WebDriver driver) {
		super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);		
	}
	
	public By getByWithKey(String key) {
		By val=null;
		switch(key) {
		case "Signup":
			val = bySignup;
			break;
		case "Any Thing":
			val = null;
			break;	
		}
		return val;		
		}
}
