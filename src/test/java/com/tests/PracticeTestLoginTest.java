package com.tests;

import static com.webDriver.GlobalDriver.getGlobalDriver;
import static com.webDriver.GlobalDriver.selectBrowser;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.pages.PracticeTestLoginPage;
import com.utils.Print;
import com.utils.ResponseFromPage;

/**
 * Test class for Practice Test Automation Login Page
 * Contains 3 test cases:
 * 1. Positive LogIn test
 * 2. Negative username test
 * 3. Negative password test
 * 
 * All tests execute in parallel on Chrome browser
 */
public class PracticeTestLoginTest extends Print {
	public WebDriver driver;
	private PracticeTestLoginPage loginPage;
	ResponseFromPage responseFromPage;
	public String log = "";

	@BeforeClass
	public void setup(ITestContext context) {
		System.out.println("========== Setting up test: " + context.getName() + " ==========");
		// Setup Chrome browser in parallel mode
		selectBrowser("chrome", context.getSuite().getXmlSuite().getParallel().toString(), context.getName());
		driver = getGlobalDriver().getDriver();
		super.driver = driver;
		loginPage = new PracticeTestLoginPage(driver);
		System.out.println("Browser initialized for test: " + context.getName());
	}

	@BeforeMethod
	public void refreshLog() {
		log = "";
	}

	/**
	 * Test Case 1: Positive LogIn test
	 * Steps:
	 * 1. Open login page
	 * 2. Enter username 'student'
	 * 3. Enter password 'Password123'
	 * 4. Click Submit button
	 * 5. Verify new page URL contains practicetestautomation.com/logged-in-successfully/
	 * 6. Verify page contains 'Congratulations' or 'successfully logged in'
	 * 7. Verify Log out button is displayed
	 */
	@Test(priority = 1)
	public void testPositiveLogIn() {
		System.out.println("\n========== Test Case 1: Positive LogIn Test ==========");

		// Step 1: Open login page
		System.out.println("Step 1: Opening login page");
		responseFromPage = loginPage.openLoginPage();
		log();

		// Step 2: Enter username 'student'
		System.out.println("Step 2: Entering username 'student'");
		responseFromPage = loginPage.enterUsername("student");
		log();

		// Step 3: Enter password 'Password123'
		System.out.println("Step 3: Entering password 'Password123'");
		responseFromPage = loginPage.enterPassword("Password123");
		log();

		// Step 4: Click Submit button
		System.out.println("Step 4: Clicking Submit button");
		responseFromPage = loginPage.clickSubmitButton();
		log();

		// Step 5: Verify new page URL contains practicetestautomation.com/logged-in-successfully/
		System.out.println("Step 5: Verifying success page URL");
		responseFromPage = loginPage.verifySuccessPageUrl();
		log();

		// Step 6: Verify page contains expected text
		System.out.println("Step 6: Verifying page contains success message");
		responseFromPage = loginPage.verifySuccessPageText();
		log();

		// Step 7: Verify Log out button is displayed
		System.out.println("Step 7: Verifying Log out button is displayed");
		responseFromPage = loginPage.verifyLogoutButtonDisplayed();
		log();

		System.out.println("✓ Test Case 1 completed!\n");
	}

	/**
	 * Test Case 2: Negative username test
	 * Steps:
	 * 1. Open login page
	 * 2. Enter username 'incorrectUser'
	 * 3. Enter password 'Password123'
	 * 4. Click Submit button
	 * 5. Verify error message is displayed
	 * 6. Verify error message text is 'Your username is invalid!'
	 */
	@Test(priority = 2)
	public void testNegativeUsernameLogi() {
		System.out.println("\n========== Test Case 2: Negative Username Test ==========");

		// Step 1: Open login page
		System.out.println("Step 1: Opening login page");
		responseFromPage = loginPage.openLoginPage();
		log();

		// Step 2: Enter incorrect username
		System.out.println("Step 2: Entering incorrect username 'incorrectUser'");
		responseFromPage = loginPage.enterUsername("incorrectUser");
		log();

		// Step 3: Enter correct password
		System.out.println("Step 3: Entering password 'Password123'");
		responseFromPage = loginPage.enterPassword("Password123");
		log();

		// Step 4: Click Submit button
		System.out.println("Step 4: Clicking Submit button");
		responseFromPage = loginPage.clickSubmitButton();
		log();

		// Step 5: Verify error message is displayed
		System.out.println("Step 5: Verifying error message is displayed");
		responseFromPage = loginPage.verifyErrorMessageDisplayed();
		log();

		// Step 6: Verify error message text
		System.out.println("Step 6: Verifying error message text is 'Your username is invalid!'");
		responseFromPage = loginPage.verifyErrorMessageText("Your username is invalid!");
		log();

		System.out.println("✓ Test Case 2 completed!\n");
	}

	/**
	 * Test Case 3: Negative password test
	 * Steps:
	 * 1. Open login page
	 * 2. Enter username 'student'
	 * 3. Enter incorrect password 'incorrectPassword'
	 * 4. Click Submit button
	 * 5. Verify error message is displayed
	 * 6. Verify error message text is 'Your password is invalid!'
	 */
	@Test(priority = 3)
	public void testNegativePasswordLogin() {
		System.out.println("\n========== Test Case 3: Negative Password Test ==========");

		// Step 1: Open login page
		System.out.println("Step 1: Opening login page");
		responseFromPage = loginPage.openLoginPage();
		log();

		// Step 2: Enter correct username
		System.out.println("Step 2: Entering username 'student'");
		responseFromPage = loginPage.enterUsername("student");
		log();

		// Step 3: Enter incorrect password
		System.out.println("Step 3: Entering incorrect password 'incorrectPassword'");
		responseFromPage = loginPage.enterPassword("incorrectPassword");
		log();

		// Step 4: Click Submit button
		System.out.println("Step 4: Clicking Submit button");
		responseFromPage = loginPage.clickSubmitButton();
		log();

		// Step 5: Verify error message is displayed
		System.out.println("Step 5: Verifying error message is displayed");
		responseFromPage = loginPage.verifyErrorMessageDisplayed();
		log();

		// Step 6: Verify error message text
		System.out.println("Step 6: Verifying error message text is 'Your password is invalid!'");
		responseFromPage = loginPage.verifyErrorMessageText("Your password is invalid!");
		log();

		System.out.println("✓ Test Case 3 completed!\n");
	}

	/**
	 * Helper method to log response and assert
	 */
	public void log() {
		assertTrue(responseFromPage.isTrue(), responseFromPage.getMessage());
		log = logExtent(log, responseFromPage.getMessage());
	}
}

