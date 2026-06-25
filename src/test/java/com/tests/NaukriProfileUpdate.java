package com.tests;

import com.pages.NaukriLoginPage;
import com.pages.PageTemplate;
import com.utils.Constants;
import com.utils.ModuleInfo;
import com.utils.Print;
import com.utils.ResponseFromPage;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static com.webDriver.GlobalDriver.getGlobalDriver;
import static com.webDriver.GlobalDriver.selectBrowser;
import static org.testng.Assert.assertTrue;


@ModuleInfo(
        module = "Test naukri.com Profile Management Module",
        description = "Covers profile headline edit, resume update and save scenarios"
)

public class NaukriProfileUpdate extends Print {
	public WebDriver driver;
    NaukriLoginPage naukriLoginPage;
    ResponseFromPage responseFromPage;
	 PageTemplate pageTemplate;
	 public String log="";

	 	
	@BeforeClass
	@Parameters("browser")
	public void boxAppLoginPageSteps(String browser, ITestContext context) throws Exception {
		selectBrowser(browser,context.getSuite().getXmlSuite().getParallel().toString(),context.getName());
		driver = getGlobalDriver().getDriver();
		super.driver = driver;
        naukriLoginPage = new NaukriLoginPage(driver);
		pageTemplate = new PageTemplate(driver);
	}
	@BeforeMethod
	public void refreshLog() {
		log="";	
	}
	@Test(priority = 1, description = "Verify user can launch the Naukri application successfully")
	public void navigateToNaukriApp()throws Throwable {
		responseFromPage=pageTemplate.navigateToApps(Constants.NAUKRI_APP_URL);
		log();
		}

	@Parameters({"emailAddress","password"})
	@Test(priority = 2, description = "Verify registered user can log in with valid credentials and update profile headline")
	public void login(String emailAddress, String password)throws Throwable {
        responseFromPage=pageTemplate.clickButton("Login Button",naukriLoginPage.getByWithKey("Login Button"));
        log();
		responseFromPage=pageTemplate.verifyTitle("Jobseeker's Login: Search the Best Jobs available in India & Abroad - Naukri.com");
		log();
		responseFromPage=pageTemplate.enterDetails(emailAddress,"Email Id",naukriLoginPage.getByWithKey("Email Id"));
		log();
		responseFromPage=pageTemplate.enterDetails(password,"Password",naukriLoginPage.getByWithKey("Password"));
		log();
		responseFromPage=pageTemplate.clickButton("Login",naukriLoginPage.getByWithKey("Login"));
		log();
        responseFromPage=naukriLoginPage.editResumeHeadline();
        log();
        responseFromPage=naukriLoginPage.saveProfile();
        log();
		}
	public void log() {
		assertTrue(responseFromPage.isTrue(),responseFromPage.getMessage());
		log = logExtent(log,responseFromPage.getMessage());
	}
}
