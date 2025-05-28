package com.controls;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Constants;
import com.utils.Print;

public class SeleniumControls extends Print {
	 WebDriver driver;
	 WebDriverWait  wait;
	 JavascriptExecutor js;
	 Actions action;
	 Alert alert;
	public SeleniumControls(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT));
		js = (JavascriptExecutor) driver;
		action = new Actions(driver);
		super.driver = driver;
	}
	protected void navigateToApp(String url) {
		driver.get(url);
	}
	protected boolean isUrl(String url) {
		boolean isMatch = false;
		isMatch = wait.until(ExpectedConditions.urlToBe(url));
		return isMatch;
	}
	protected boolean verifyPageTitle(String title) {
		String message = "Waiting for title to be  " + title
		+ "will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
		boolean isMatch = false;
		try {
			System.out.println(message);
			isMatch = wait.until(ExpectedConditions.titleIs(title));
		} catch (Exception e) {
			log(e.getMessage());
			capture("title doesnot match");
		}
		System.out.println(isMatch?driver.getTitle():"wrong title: "+driver.getTitle());
		return isMatch;
	}

	protected String getPageTitle()
	{
		String pgTitle=driver.getTitle();
		return pgTitle;
	}
	protected void switchToActiveWindow()throws Exception {
	     ArrayList<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
	     driver.switchTo().window(windowHandles.get(windowHandles.size()-1));
	}
	protected void switchToOriginalWindow() {
	     ArrayList<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
	     driver.switchTo().window(windowHandles.get(0));
	}
	protected void browseBack() {
	     driver.navigate().back();
	}
	protected void refresh() {
		driver.navigate().refresh();
	}
	protected void click(WebElement element) throws Exception {
		highLightElement(element);
		element.click();
	}
	protected String getAttribute(WebElement element, String attribute) throws Exception {
		highLightElement(element);
		String val="";
		if (attribute != null) 
			val = element.getAttribute(attribute);	
		return val;	
	}
	protected void contextClick(WebElement element) throws Exception {
		highLightElementHasStyle(element);
		action.contextClick(element).build().perform();
	}
	protected void moveToElement(WebElement element) throws Exception {
		action.moveToElement(element).build().perform();
		highLightElementHasStyle(element);
	}
	protected void scrollToElement(WebElement element) throws Exception {
		action.scrollToElement(element).build().perform();
		highLightElementHasStyle(element);
	}
	protected void scrollDown() {
		action.sendKeys(Keys.PAGE_DOWN).build().perform();
	}
	protected void scrollUp() {
		action.sendKeys(Keys.PAGE_UP).build().perform();
	}
	protected void pressEsc() {
		action.sendKeys(Keys.ESCAPE).build().perform();
	}
	protected void arrowLeft() {
		action.sendKeys(Keys.ARROW_LEFT).build().perform();
	}
	protected void arrowRight() {
		action.sendKeys(Keys.ARROW_RIGHT).build().perform();
	}
	protected void clickControlAndChar(char key){
		action.keyDown( Keys.CONTROL ).sendKeys( String. valueOf(key) ).keyUp( Keys.CONTROL ).build().perform();
	}
	protected void sendKeys(String keys){
		action.sendKeys( keys ).build().perform();
	}	
	protected WebElement isElementVisibleBy(By by) {
		String name = by.toString();
		String message = "Searching element " + name
		+ " for presence , will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
		WebElement element=null;
		try {
			System.out.println(message);
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			log(e.getMessage());
			capture("element_not_present");			
		}
		return  element;
	}
	
	protected boolean isInVisible(WebElement element) {
		String name = element.toString();
		String message = "Searching element " + name
		+ " for invisibility , will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
		boolean isInvisible=false;
		try {
			System.out.println(message);
			isInvisible = wait.until(ExpectedConditions.invisibilityOf(element));
		} catch (Exception e) {
			log(e.getMessage());
			capture("element_not_present");			
		}
		return  isInvisible;
	}
	protected WebElement isPresent(By by) {
		String name = by.toString();
		String message = "Searching element " + name
		+ " for presence, will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
		WebElement element=null;
		try {
			System.out.println(message);
			element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		} catch (Exception e) {
			log(e.getMessage());
			capture("element_not_clickable");			
		}
		return  element;
	}
	protected WebElement isClickable(By by) {
		String name = by.toString();
		String message = "Searching element " + name
		+ " for presence and clickability, will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
		WebElement element=null;
		try {
			System.out.println(message);
			element = wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			log(e.getMessage());
			capture("element_not_clickable");			
		}
		return  element;
	}
	protected boolean isAlertPresent() throws Exception {
		String message = "waiting for alert "
		+ " will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
		Alert alert = null;
		boolean isPresent = false;
		try {
			System.out.println(message);
			alert= wait.until(ExpectedConditions.alertIsPresent());
			if (alert!=null)
				isPresent=true;
		} catch (Exception e) {
			log(e.getMessage());
			capture("element_not_clickable");
		}
		return isPresent;
	}
	protected void switchToAlert() {
		alert = driver.switchTo().alert();		
	}
	protected String getAlertText() {
		return alert.getText();
	}
	protected void acceptAlert() {
		alert.accept();
	}
	protected void dismissAlert() {
		alert.dismiss();
	}	
	protected void sendKeysAlert(String keys) {
		alert.sendKeys(keys);
	}
	
	protected List<WebElement> verifyNumberOfElementsToBe(By by, int number) {
		String name = by.toString();
		String message = "Searching elements list " + name
		+ " to be equal to "+number+", will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
	
		List<WebElement> elementList = null;
		try {
			System.out.println(message);
			elementList = wait.until(ExpectedConditions.numberOfElementsToBe(by,number));
		} catch (Exception e) {
			elementList = null;
			log(e.getMessage());
			capture("no element  "+name);
		}
		return elementList;
	}	
	protected WebElement waitAndFindElementByVisibility(WebElement element) {
		String name = element.toString();
		String message = "Searching element " + name
		+ " for presence, will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
		try {
			System.out.println(message);
			element= wait.until(ExpectedConditions.visibilityOf(element));
			highLightElementHasStyle(element);
		} catch (Exception e) {
			log(e.getMessage());
			capture("element not visible  "+name);
		}
		return element;
	}
	protected List<WebElement> waitAndFindAllElementsByVisibility(By by) {
		String name = by.toString();
		String message = "Searching elements list " + name
		+ ", will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
		+ " seconds";
	
		List<WebElement> elementList = null;
		try {
			System.out.println(message);
			elementList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
		} catch (Exception e) {
			elementList = null;
			log(e.getMessage());
			capture("no elements  "+name);
		}
		return elementList;
	}
	protected boolean switchToFrameBy(By by){
		String message = "Waiting to switch to frame"
				+ ", will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
				+ " seconds";
		boolean isSwitched = false; 
		try {
			System.out.println(message);
			Thread.sleep(2000);
			this.driver = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
			isSwitched = true;
		} catch (Exception e) {
			log(e.getMessage());
			capture("frameSwitch");
		}
		return isSwitched;
	}
	protected boolean switchToFrameBy(WebElement element){
		String message = "Waiting to switch to frame"
				+ ", will wait by " + Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT
				+ " seconds";
		boolean isSwitched = false; 
		try {
			System.out.println(message);
			Thread.sleep(2000);
			this.driver = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
			isSwitched = true;
		} catch (Exception e) {
			log(e.getMessage());
			capture("frameSwitch");
		}
		return isSwitched;
	}	

	protected void returnFromFrame(){
		this.driver = driver.switchTo().parentFrame();	
	}
	protected String currntIframe() {
		JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
		return jsExecutor.executeScript("return self.name").toString();
	}
	private void highLightElement(WebElement element) throws InterruptedException{
		js.executeScript("arguments[0].setAttribute('style','border: 2px solid red;');", element);
		Thread.sleep(150);
		js.executeScript("arguments[0].style.border=''", element, "");
	}
	private void highLightElementHasStyle(WebElement element) throws InterruptedException{
		String originalStyle = (String)js.executeScript("return arguments[0].getAttribute('style');", element);
		String modifiedStyle = originalStyle+" border-color: red;";
		js.executeScript("arguments[0].setAttribute('style',arguments[1]);", element,modifiedStyle);
		Thread.sleep(150);
		js.executeScript("arguments[0].setAttribute('style',arguments[1]);", element,originalStyle);
	}
}
