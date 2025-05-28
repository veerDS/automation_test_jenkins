package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.controls.SeleniumControls;
import com.utils.ResponseFromPage;

public class PageTemplate extends SeleniumControls {
	 WebDriver driver;
	public PageTemplate(WebDriver driver) {
		super(driver);
        this.driver = driver;
	}
	public ResponseFromPage verifyTitle(String title) {
		if(verifyPageTitle(title)==true) {
			return new ResponseFromPage(true,"Title verified: "+title);
		}
		else {
			return new ResponseFromPage(false,"Wrong title: "+title);
		}
	}
	public ResponseFromPage getTitle() {
		if(getPageTitle()!=null) {
			return new ResponseFromPage(true,getPageTitle());
		}
		else {
			return new ResponseFromPage(false,"title doesnot exist");
		}
	}
	public ResponseFromPage verifyTitleOfActiveWindow(String title)throws Exception {
		switchToActiveWindow();
		Thread.sleep(1000);
		if(verifyPageTitle(title)==true) {
			return new ResponseFromPage(true,"Title verified: "+title);
		}
		else {
			return new ResponseFromPage(false,"Wrong title: "+title);
		}
		}
	public ResponseFromPage browseBack(String previousPageUrl) {
		browseBack();
		boolean isUrl = isUrl(previousPageUrl);
		if(isUrl==true) {
			return new ResponseFromPage(true,"Successfully switched back to: "+previousPageUrl);
		}
		else {
			return new ResponseFromPage(false,"Unable to switch  back to: "+previousPageUrl);
		}
		}
	public ResponseFromPage refreshPage(String title)throws Exception {
		refresh();
		Thread.sleep(1000);
		if(verifyPageTitle(title)==true) {
			return new ResponseFromPage(true,"Page refreshed and Title verified: "+title);
		}
		else {
			return new ResponseFromPage(false,"Page cannot be refreshed Wrong title: "+title);
		}
		}
	public ResponseFromPage verifyTitleOfOriginalWindow(String title)throws Exception {
		switchToOriginalWindow();
		Thread.sleep(1000);
		if(verifyPageTitle(title)==true) {
			return new ResponseFromPage(true,"Title verified: "+title);
		}
		else {
			return new ResponseFromPage(false,"Wrong title: "+title);
		}
	}
	public ResponseFromPage navigateToApps(String url) {
		navigateToApp(url);
		boolean isUrl= isUrl(url);
		if(isUrl==true) {
			return new ResponseFromPage(true,"url: "+url+ " is available");
		}
		else {
			return new ResponseFromPage(false,"url: "+url+ " is not valid");
		}
		}
	public ResponseFromPage  enterDetails(String details,String key,By by)throws Exception {
		WebElement element = isClickable(by);
		if(element!=null){
			click(element);
			sendKeys(details);
			return new ResponseFromPage(true,key+":"+details+" is entered");
		}
		else {
			return new ResponseFromPage(false,key+" cannot be clicked");
		}
		}
	public ResponseFromPage clickButton(String key,By by) throws Exception {
		WebElement element = isClickable(by);
		if(element!=null){
			click(element);
			return new ResponseFromPage(true,key+" button is clicked");
		}
		else {
			return new ResponseFromPage(false,key+" button cannot be clicked");
		}			
		}
	public ResponseFromPage select(String key,By by) throws Exception {
		WebElement element = isClickable(by);
		if(element!=null){
			click(element);
			return new ResponseFromPage(true,key+" is selected");
		}
		else {
			return new ResponseFromPage(false,key+" cannot be selected");
		}			
		}
}
