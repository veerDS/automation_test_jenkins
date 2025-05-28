package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.controls.SeleniumControls;
import com.utils.ResponseFromPage;

public class BoxAppDevelopersPage extends SeleniumControls {
	 WebDriver driver;
	 WebDriverWait  wait;	 
	 By byApp = By.xpath("//div[@class='NameCell-title']");
	 By byConfiguration = By.xpath("//Span[text()='Configuration']");
	 By byGenerateDeveloperToken = By.xpath("//Span[text()='Generate Developer Token']");
	 By byDeveloperToken = By.xpath("//input[@name='devToken']");
	 
	public BoxAppDevelopersPage(WebDriver driver) {
		super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);		
		}
	
	public ResponseFromPage isDeveloperTokenAvailable(String key) throws Exception {
		WebElement element = isElementVisibleBy(getByWithKey(key));
		if(element!=null){
			click(element);
			return new ResponseFromPage(true,"Developer token is avialable");
		}
		else {
			return new ResponseFromPage(false,"Developer token is not avialable");
		}			
		}
	public ResponseFromPage copyDeveloperToken(String key) throws Exception {
		WebElement element = isClickable(getByWithKey(key));
		if(element!=null){
			return new ResponseFromPage(true,"Developer token is avialable",getAttribute(element, "value"));
		}
		else {
			return new ResponseFromPage(false,"Developer token is not avialable");
		}			
		}	
	public By getByWithKey(String key) {
		By val=null;
		switch(key) {
		case "App":
			val = byApp;
			break;
		case "Configuration":
			val = byConfiguration;
			break;
		case "Generate Developer Token":
			val = byGenerateDeveloperToken;
			break;
		case "Developer Token":
			val = byDeveloperToken;
			break;			
		}
		return val;		
		}
}
