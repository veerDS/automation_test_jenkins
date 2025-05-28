package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.controls.SeleniumControls;
import com.utils.ResponseFromPage;

public class BoxAppHomePage extends SeleniumControls {
	 WebDriver driver;
	 WebDriverWait  wait;
	 String folderId="";
	 String xpathPrefix = "//div[@data-resin-folder_id='";
	 String xpathSuffix = "']//input[@name='select-checkbox']";
	 By byDevConsole = By.xpath("//a[@aria-label='Dev Console']");
	 By byNew = By.cssSelector("button.create-dropdown-menu-toggle-button:not([aria-disabled]");
	 By byCreateFolder = By.xpath("//li[@aria-label='Create a new Folder']");
	 By byCreate = By.xpath("//button[@data-resin-target='primarybutton']");
	 By byTrash = By.xpath("//button[@data-resin-target='trash']");
	 By byOk = By.xpath("//button[@data-resin-target='primarybutton']");
	 By byProfile = By.xpath("//button[@data-resin-target='accountmenu']");
	 By byLogOut = By.xpath("//a[@data-resin-target='logout']");
	 By byFolderName = By.xpath("//input[@name='folder-name']");
	 By byCreateSuccess = By.xpath("//div[@class='notification info wrap notification-enter-done']");
	 By byMessage = By.cssSelector("div.notification.info.wrap");
	 By bySort = By.xpath("//a[@data-resin-target='sortbydate']");
	 By byFolderCreated = By.xpath("//div[@data-item-index='0']");
		 	 	 
	public BoxAppHomePage(WebDriver driver) {
		super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);		
		}
	
	public ResponseFromPage isInvisible(String key) {
		WebElement element = isPresent(getByWithKey(key));
		boolean isInvisible = false;
		if(element!=null){
			isInvisible = isInVisible(element);
			if(isInvisible==true) {
				return new ResponseFromPage(true,key+" has dissapeared");	
			}
			else {
				return new ResponseFromPage(true,key+" still present");	
			}	
		}
		else {
			return new ResponseFromPage(false,"folder has not yet created/deleted");
		}
	}
	public ResponseFromPage checkSort() throws Exception {
		WebElement element = isClickable(bySort);
		if(element!=null){
			String sortBy = getAttribute(element,"aria-sort");
			if (sortBy.equals("ascending"))	
			click(element);
			return new ResponseFromPage(true," sorted successfully");
		}
		else {
			return new ResponseFromPage(false," cannot sort");
		}			
		}
	public ResponseFromPage getFolderId() throws Exception {
		WebElement element = isPresent(byFolderCreated);
		if(element!=null){
			folderId = getAttribute(element,"data-resin-folder_id");
			return new ResponseFromPage(true,"folder id is "+folderId);
		}
		else {
			return new ResponseFromPage(false,"cannot get folder id");
		}			
		}
	public ResponseFromPage selectFolder() throws Exception {
		WebElement element = isPresent(By.xpath(xpathPrefix+folderId+xpathSuffix));
		if(element!=null){
			moveToElement(element);
			Thread.sleep(2000);
			click(element);
			return new ResponseFromPage(true,"folder selected successfully");
		}
		else {
			return new ResponseFromPage(false,"folder cannot be selected");
		}			
		}
	public By getByWithKey(String key) {
		By val=null;
		switch(key) {
		case "Dev Console":
			val = byDevConsole;
			break;
		case "New":
			val = byNew;
			break;
		case "Create Folder":
			val = byCreateFolder;
			break;
		case "Create":
			val = byCreate;
			break;
		case "Trash":
			val = byTrash;
			break;
		case "Ok":
			val = byOk;
			break;
		case "Profile":
			val = byProfile;
			break;
		case "LogOut":
			val = byLogOut;
			break;
		case "Folder Name":
			val = byFolderName;
			break;
		case "Success message":
			val = byMessage;
			break;
		case "Delete message":
			val = byMessage;
			break;	
		}
		return val;		
		}
}
