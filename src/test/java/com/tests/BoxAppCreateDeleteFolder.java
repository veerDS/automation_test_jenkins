package com.tests;

import static com.webDriver.GlobalDriver.getGlobalDriver;
import static com.webDriver.GlobalDriver.selectBrowser;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.WebDriver;
//import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pages.BoxAppHomePage;
import com.pages.BoxAppLoginPage;
import com.pages.PageTemplate;
import com.utils.Constants;
import com.utils.Print;
import com.utils.ResponseFromPage;

public class BoxAppCreateDeleteFolder extends Print {
	 public WebDriver driver;
	 BoxAppLoginPage boxAppLoginPage;
	 BoxAppHomePage boxAppHomePage;
	 PageTemplate pageTemplate;
	 ResponseFromPage responseFromPage;
	 public String log="";
	 
	 @BeforeClass
	 @Parameters("browser") 
	 public void boxAppCreateFolderSteps(String browser,ITestContext context) {
		 selectBrowser(browser,context.getSuite().getXmlSuite().getParallel().toString(),context.getName());
		driver = getGlobalDriver().getDriver();
		super.driver = driver;
		boxAppLoginPage = new BoxAppLoginPage(driver);
		boxAppHomePage = new BoxAppHomePage(driver);
		pageTemplate = new PageTemplate(driver);
		}
	@BeforeMethod
	public void refreshLog() {
		log="";	
	}
	@Test(priority = 1)
	public void navigateToBox()throws Throwable {
		responseFromPage=pageTemplate.navigateToApps(Constants.BOX_APP_URL);
		log();
		}
	@Parameters({"emailAddress","password"})
	@Test(priority = 2)
	public void loginCreate(String emailAddress, String password)throws Throwable {		
		responseFromPage=pageTemplate.verifyTitle("Box | Login");
		log();
		responseFromPage=pageTemplate.enterDetails(emailAddress,"Email Id",boxAppLoginPage.getByWithKey("Email Id"));
		log();
		responseFromPage=pageTemplate.clickButton("Next",boxAppLoginPage.getByWithKey("Next"));
		log();
		responseFromPage=pageTemplate.enterDetails(password,"Password",boxAppLoginPage.getByWithKey("Password"));
		log();
		responseFromPage=pageTemplate.clickButton("Login",boxAppLoginPage.getByWithKey("Login"));
		log();
		}
	@Test(priority = 3)
	public void createFolder()throws Throwable {
		responseFromPage=pageTemplate.verifyTitle("All Files | Powered by Box");
		log();
		responseFromPage=pageTemplate.clickButton("New",boxAppHomePage.getByWithKey("New"));
		log();
		responseFromPage=pageTemplate.clickButton("Create Folder",boxAppHomePage.getByWithKey("Create Folder"));
		log();
		responseFromPage=pageTemplate.enterDetails(uniqueName(),"Folder Name",boxAppHomePage.getByWithKey("Folder Name"));
		log();
		responseFromPage=pageTemplate.clickButton("Create",boxAppHomePage.getByWithKey("Create"));
		log();
		responseFromPage=boxAppHomePage.isInvisible("Success message");
		log();
		responseFromPage=boxAppHomePage.getFolderId();
		log();
		}
	@Test(priority = 4)
	public void logOutCreate()throws Throwable {
		responseFromPage=pageTemplate.clickButton("Profile",boxAppHomePage.getByWithKey("Profile"));
		log();
		Thread.sleep(1000);
		responseFromPage=pageTemplate.clickButton("LogOut",boxAppHomePage.getByWithKey("LogOut"));
		log();
		Thread.sleep(1000);
		}
	@Parameters({"emailAddress","password"})
	@Test(priority = 5)
	public void loginDelete(String emailAddress, String password)throws Throwable {		
		responseFromPage=pageTemplate.verifyTitle("Box | Login");
		log();
		responseFromPage=pageTemplate.enterDetails(emailAddress,"Email Id",boxAppLoginPage.getByWithKey("Email Id"));
		log();
		responseFromPage=pageTemplate.clickButton("Next",boxAppLoginPage.getByWithKey("Next"));
		log();
		responseFromPage=pageTemplate.enterDetails(password,"Password",boxAppLoginPage.getByWithKey("Password"));
		log();
		responseFromPage=pageTemplate.clickButton("Login",boxAppLoginPage.getByWithKey("Login"));
		log();
		}
	@Test(priority = 6)
	public void deleteFolder()throws Throwable {
		responseFromPage=boxAppHomePage.checkSort();
		log();
		Thread.sleep(1000);
		responseFromPage=boxAppHomePage.selectFolder();
		log();
		responseFromPage=pageTemplate.clickButton("Trash",boxAppHomePage.getByWithKey("Trash"));
		log();
		responseFromPage=pageTemplate.clickButton("Ok",boxAppHomePage.getByWithKey("Ok"));
		log();
		responseFromPage=boxAppHomePage.isInvisible("Delete message");
		log();	
		}
	@Test(priority = 7)
	public void logOutDelete()throws Throwable {
		responseFromPage=pageTemplate.clickButton("Profile",boxAppHomePage.getByWithKey("Profile"));
		log();
		Thread.sleep(1000);
		responseFromPage=pageTemplate.clickButton("LogOut",boxAppHomePage.getByWithKey("LogOut"));
		log();
		Thread.sleep(1000);
		}
	public static String uniqueName() {
		long val = (long)((1-Math.random())*1000);
		return "Folder_"+Long.toString(val);
	}
	public void log() {
		assertTrue(responseFromPage.isTrue(),responseFromPage.getMessage());
		log = logExtent(log,responseFromPage.getMessage());
	}
}
