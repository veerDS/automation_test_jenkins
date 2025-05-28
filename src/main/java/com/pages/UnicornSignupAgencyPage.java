package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.controls.SeleniumControls;

public class UnicornSignupAgencyPage extends SeleniumControls {
	 WebDriver driver;
	 String xpathPrefixRange="//span[text()='";
	 String xpathSuffixRange="']";
	 String xpathPrefixText="//ng-dropdown-panel//label[text()=' ";
	 String xpathSuffixText=" ']";
	 By byAgencyName = By.xpath("//input[@placeholder='Enter Agency Name']");
	 By byAgencyAddress = By.xpath("//input[@placeholder='Enter Agency Street Address']");
	 By byCity = By.xpath("//input[@placeholder='City']");
	 By byState = By.xpath("//ng-select[@ng-reflect-name='state']");
	 By byZipCode = By.xpath("//input[@id='zip']");
	 By byBusinessType = By.xpath("//ng-select[@ng-reflect-name='businessTypes']");
	 By byDealsType = By.xpath("//ng-select[@ng-reflect-name='transactionsType']");
	 By byBusinessLocation = By.xpath("//ng-select[@ng-reflect-name='underwritersBusiness']");
	 By byDealingStates = By.xpath("//ng-select[@ng-reflect-name='businessStates']");
	 By byNumberOfEmployes = By.xpath("//ng-select[@ng-reflect-name='usersRange']");
	 By byNext = By.xpath("//button[text()=' NEXT ']");	 
	public UnicornSignupAgencyPage(WebDriver driver) {
		super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);		
	}
	
	public By getByWithKey(String key) {
		By val=null;
		switch(key) {
		case "Agency Name":
			val = byAgencyName;
			break;
		case "Agency Address":
			val = byAgencyAddress;
			break;
		case "City":
			val = byCity;
			break;
		case "State":
			val = byState;
			break;
		case "Zip Code":
			val = byZipCode;
			break;
		case "Business Type":
			val = byBusinessType;
			break;
		case "Deals Type":
			val = byDealsType;
			break;
		case "Business Location":
			val = byBusinessLocation;
			break;
		case "Dealing States":
			val = byDealingStates;
			break;
		case "Number Of Employes":
			val = byNumberOfEmployes;
			break;
		case "Next":
			val = byNext;
			break;		
		}
		return val;		
		}
	public By getByWithKey(String value,String key) {
		By val=null;
		switch(key) {
		case "Range":
			val = By.xpath(xpathPrefixRange+value+xpathSuffixRange);
			break;
		case "Text":
			val = By.xpath(xpathPrefixText+value+xpathSuffixText);
			break;
		}
		return val;
		}
}
