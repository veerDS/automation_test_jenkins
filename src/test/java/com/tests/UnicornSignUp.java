package com.tests;

import static com.webDriver.GlobalDriver.getGlobalDriver;
import static com.webDriver.GlobalDriver.selectBrowser;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pages.PageTemplate;
import com.pages.UnicornHomePage;
import com.pages.UnicornSignupAgencyPage;
import com.pages.UnicornSignupSelfPage;
import com.utils.Constants;
import com.utils.DataAccess;
import com.utils.Print;
import com.utils.ResponseFromPage;

public class UnicornSignUp extends Print {
	public WebDriver driver;
	 ResponseFromPage responseFromPage;
	 UnicornHomePage unicornHomePage;
	 UnicornSignupAgencyPage signupAgencyPage;
	 UnicornSignupSelfPage signupSelfPage;
	 PageTemplate pageTemplate;
	 public String log="";

	 	
	@BeforeClass
	@Parameters("browser")
	public void boxAppLoginPageSteps(String browser, ITestContext context) {
		selectBrowser(browser,context.getSuite().getXmlSuite().getParallel().toString(),context.getName());
		driver = getGlobalDriver().getDriver();
		super.driver = driver;
		unicornHomePage = new UnicornHomePage(driver);
		signupAgencyPage = new UnicornSignupAgencyPage(driver);
		signupSelfPage = new UnicornSignupSelfPage(driver);
		pageTemplate = new PageTemplate(driver);
	}
	@BeforeMethod
	public void refreshLog() {
		log="";	
	}
	@Test(priority = 1)
	public void navigateToUnicornLogin()throws Throwable {
		responseFromPage=pageTemplate.navigateToApps(Constants.UNICORN_APP_URL);
		log();
		}
	@Test(priority = 2)
	public void navigateToSignupPage()throws Throwable {
		responseFromPage=pageTemplate.verifyTitle("UNICORN");
		log();
		responseFromPage=pageTemplate.clickButton("Signup",unicornHomePage.getByWithKey("Signup"));
		log();
		responseFromPage=pageTemplate.verifyTitle("UNICORN");
		log();
		}

	
	
	@Test(groups = "smoke", priority = 3,dataProvider="unicornSignup", dataProviderClass = DataAccess.class)
	public void signUp(String agencyName,String agencyAddress,String city,String state,String zipCode,
			String businessType,String dealsType,String businessLocation,String dealingStates,
			String noOfEmployes,String firstName,String lastName,String emailAddress )throws Throwable {
		responseFromPage=pageTemplate.refreshPage("UNICORN");
		log();
		responseFromPage=pageTemplate.enterDetails(agencyName,"Agency Name",signupAgencyPage.getByWithKey("Agency Name"));
		log();
		responseFromPage=pageTemplate.enterDetails(agencyAddress,"Agency Address",signupAgencyPage.getByWithKey("Agency Address"));
		log();
		responseFromPage=pageTemplate.enterDetails(city,"City",signupAgencyPage.getByWithKey("City"));
		log();
		responseFromPage=pageTemplate.clickButton("State",signupAgencyPage.getByWithKey("State"));
		log();
		responseFromPage=pageTemplate.select(state,signupAgencyPage.getByWithKey(state,"Range"));
		log();
		responseFromPage=pageTemplate.enterDetails(zipCode,"Zip Code",signupAgencyPage.getByWithKey("Zip Code"));
		log();
		responseFromPage=pageTemplate.clickButton("Business Type",signupAgencyPage.getByWithKey("Business Type"));
		log();
		responseFromPage=pageTemplate.select(businessType,signupAgencyPage.getByWithKey(businessType,"Text"));
		log();
		responseFromPage=pageTemplate.clickButton("Deals Type",signupAgencyPage.getByWithKey("Deals Type"));
		log();
		responseFromPage=pageTemplate.select(dealsType,signupAgencyPage.getByWithKey(dealsType,"Text"));
		log();
		responseFromPage=pageTemplate.clickButton("Business Location",signupAgencyPage.getByWithKey("Business Location"));
		log();
		responseFromPage=pageTemplate.select(businessLocation,signupAgencyPage.getByWithKey(businessLocation,"Text"));
		log();
		responseFromPage=pageTemplate.clickButton("Dealing States",signupAgencyPage.getByWithKey("Dealing States"));
		log();
		responseFromPage=pageTemplate.select(dealingStates,signupAgencyPage.getByWithKey(dealingStates,"Text"));
		log();
		responseFromPage=pageTemplate.clickButton("Number Of Employes",signupAgencyPage.getByWithKey("Number Of Employes"));
		log();
		responseFromPage=pageTemplate.select(noOfEmployes,signupAgencyPage.getByWithKey(noOfEmployes,"Range"));
		log();
		responseFromPage=pageTemplate.clickButton("Next",signupAgencyPage.getByWithKey("Next"));
		log();
		responseFromPage=pageTemplate.verifyTitle("UNICORN");
		log();
		responseFromPage=pageTemplate.enterDetails(firstName,"First Name",signupSelfPage.getByWithKey("First Name"));
		log();
		responseFromPage=pageTemplate.enterDetails(lastName,"Last Name",signupSelfPage.getByWithKey("Last Name"));
		log();
		responseFromPage=pageTemplate.enterDetails(emailAddress,"Email Id",signupSelfPage.getByWithKey("Email Id"));
		log();
		responseFromPage=pageTemplate.clickButton("Submit",signupSelfPage.getByWithKey("Submit"));
		log();
		responseFromPage=pageTemplate.clickButton("Congrats",signupSelfPage.getByWithKey("Congrats"));
		log();
		responseFromPage=pageTemplate.verifyTitle("UNICORN");
		log();
		responseFromPage=pageTemplate.refreshPage("UNICORN");
		log();
		Thread.sleep(1000);
		}
//	@Test(priority = 3)
//	public void logOut()throws Throwable {
//		responseFromPage=pageTemplate.clickButton("Profile",boxAppHomePage.getByWithKey("Profile"));
//		log();
//		Thread.sleep(1000);
//		responseFromPage=pageTemplate.clickButton("LogOut",boxAppHomePage.getByWithKey("LogOut"));
//		log();
//		Thread.sleep(1000);
//		}
	public void log() {
		assertTrue(responseFromPage.isTrue(),responseFromPage.getMessage());
		log = logExtent(log,responseFromPage.getMessage());
	}
}
