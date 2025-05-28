package com.tests;

import static com.webDriver.GlobalDriver.getGlobalDriver;
import static com.webDriver.GlobalDriver.selectBrowser;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pages.BoxAppDevelopersPage;
import com.pages.BoxAppHomePage;
import com.pages.BoxAppLoginPage;
import com.pages.PageTemplate;
import com.utils.Constants;
import com.utils.Print;
import com.utils.ResponseFromPage;

public class BoxAppDevelopers extends Print {
	 WebDriver driver;
	 BoxAppLoginPage boxAppLoginPage;
	 BoxAppDevelopersPage boxAppDevelopersPage;
	 BoxAppHomePage boxAppHomePage;
	 PageTemplate pageTemplate;
	 ResponseFromPage responseFromPage;
	 boolean isDeveloperTokenAvailable;
	 public String log="";
	 public String xmlTestName;
	
	@BeforeClass 
	@Parameters("browser")  
	public void boxAppDevelopersSteps(String browser,ITestContext context) {
		selectBrowser(browser,context.getSuite().getXmlSuite().getParallel().toString(),context.getName());
		driver = getGlobalDriver().getDriver();
		super.driver = driver;
		boxAppLoginPage = new BoxAppLoginPage(driver);
		boxAppDevelopersPage = new BoxAppDevelopersPage(driver);
		boxAppHomePage = new BoxAppHomePage(driver);
		pageTemplate = new PageTemplate(driver);
		isDeveloperTokenAvailable=false;
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
	public void login(String emailAddress, String password)throws Throwable {
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
	public void clickDevConsoleButton()throws Throwable {
		responseFromPage=pageTemplate.verifyTitle("All Files | Powered by Box");
		log();
		responseFromPage=pageTemplate.clickButton("Dev Console",boxAppHomePage.getByWithKey("Dev Console"));
		log();
		responseFromPage=pageTemplate.verifyTitleOfActiveWindow("Box Developers");
		log();
		responseFromPage=pageTemplate.clickButton("App",boxAppDevelopersPage.getByWithKey("App"));
		log();
		responseFromPage=pageTemplate.verifyTitle("FirstBoxDevApp | Box Developers");
		log();
		responseFromPage=pageTemplate.clickButton("Configuration",boxAppDevelopersPage.getByWithKey("Configuration"));
		log();
		}
	@Test(priority = 4)
	public void isDeveloperTokenAvailable()throws Throwable {
		responseFromPage=boxAppDevelopersPage.isDeveloperTokenAvailable("Developer Token");
		assertTrue(true,responseFromPage.getMessage());
		isDeveloperTokenAvailable=responseFromPage.isTrue();
		log = logExtent(log,responseFromPage.getMessage());		
		}
	@Test(priority = 5)
	public void clickGenerateDeveloperTokenButton()throws Throwable {
		if(isDeveloperTokenAvailable==false) {
			responseFromPage=pageTemplate.clickButton("Generate Developer Token",boxAppDevelopersPage.getByWithKey("Generate Developer Token"));
			Thread.sleep(2000);
			log();
		}
		else {
			Assert.assertTrue(true, "Developer token is available");
			log = logExtent(log,"Developer token is available");
		}
		}
	@Test(priority = 6)
	public void copyDeveloperToken() throws Throwable {
		responseFromPage=boxAppDevelopersPage.copyDeveloperToken("Developer Token");
		log();
		log = logExtent(log,"developer token is "+responseFromPage.getExtentMessage());
		Thread.sleep(1000);
		responseFromPage=pageTemplate.verifyTitleOfOriginalWindow("All Files | Powered by Box");
		log();
		Thread.sleep(1000);
		}
	@Test(priority = 7)
	public void logOut()throws Throwable {
		responseFromPage=pageTemplate.clickButton("Profile",boxAppHomePage.getByWithKey("Profile"));
		log();
		Thread.sleep(1000);
		responseFromPage=pageTemplate.clickButton("LogOut",boxAppHomePage.getByWithKey("LogOut"));
		log();
		Thread.sleep(1000);
		}
	public void log() {
		assertTrue(responseFromPage.isTrue(),responseFromPage.getMessage());
		log = logExtent(log,responseFromPage.getMessage());
	}
}
