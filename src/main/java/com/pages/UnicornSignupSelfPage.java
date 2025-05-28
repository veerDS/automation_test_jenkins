package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.controls.SeleniumControls;

public class UnicornSignupSelfPage extends SeleniumControls {
	 WebDriver driver;
	 By byFirstName = By.xpath("//input[@id='firstName']");
	 By byLastName = By.xpath("//input[@id='lastName']");
	 By byEmailId = By.xpath("//input[@id='email']");
	 By byYes = By.xpath("//label[text()='Yes']");
	 By bySubmit = By.xpath("//button[text()=' SUBMIT ']");
	 By byCongrats = By.xpath("//h3[text()='Congratulations! Your Registration is Complete']");
	 
	public UnicornSignupSelfPage(WebDriver driver) {
		super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);		
	}
	
	public By getByWithKey(String key) {
		By val=null;
		switch(key) {
		case "First Name":
			val = byFirstName;
			break;
		case "Last Name":
			val = byLastName;
			break;
		case "Email Id":
			val = byEmailId;
			break;
		case "Yes":
			val = byYes;
			break;
		case "Submit":
			val = bySubmit;
			break;
		case "Congrats":
			val = byCongrats;
			break;		
		}
		return val;		
		}
}
