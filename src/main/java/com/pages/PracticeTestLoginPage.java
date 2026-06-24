package com.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Constants;
import com.utils.ResponseFromPage;

public class PracticeTestLoginPage extends PageTemplate {
	WebDriver driver;
	WebDriverWait wait;

	// Locators for Practice Test Automation Login Page
	By usernameFieldBy = By.name("username");
	By passwordFieldBy = By.name("password");
	By submitButtonBy = By.id("submit");
	By errorMessageBy = By.id("error");
	By logoutButtonBy = By.xpath("//a[contains(text(), 'Log out')]");

	public PracticeTestLoginPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT));
		PageFactory.initElements(driver, this);
	}

	/**
	 * Navigate to the Practice Test Automation login page
	 */
	public ResponseFromPage openLoginPage() {
		try {
			navigateToApp("https://practicetestautomation.com/practice-test-login/");
			Thread.sleep(1000);
			return new ResponseFromPage(true, "Successfully navigated to login page",
					"Login page opened: https://practicetestautomation.com/practice-test-login/");
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to open login page: " + e.getMessage(),
					"Login page navigation failed");
		}
	}

	/**
	 * Enter username into the username field
	 */
	public ResponseFromPage enterUsername(String username) {
		try {
			WebElement usernameField = isClickable(usernameFieldBy);
			if (usernameField != null) {
				click(usernameField);
				sendKeys(username);
				return new ResponseFromPage(true, "Successfully entered username: " + username,
						"Username '" + username + "' entered into Username field");
			} else {
				return new ResponseFromPage(false, "Username field is not clickable",
						"Failed to interact with Username field");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to enter username: " + e.getMessage(),
					"Username entry failed");
		}
	}

	/**
	 * Enter password into the password field
	 */
	public ResponseFromPage enterPassword(String password) {
		try {
			WebElement passwordField = isClickable(passwordFieldBy);
			if (passwordField != null) {
				click(passwordField);
				sendKeys(password);
				return new ResponseFromPage(true, "Successfully entered password",
						"Password entered into Password field");
			} else {
				return new ResponseFromPage(false, "Password field is not clickable",
						"Failed to interact with Password field");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to enter password: " + e.getMessage(),
					"Password entry failed");
		}
	}

	/**
	 * Click the Submit button
	 */
	public ResponseFromPage clickSubmitButton() {
		try {
			WebElement submitButton = isClickable(submitButtonBy);
			if (submitButton != null) {
				click(submitButton);
				Thread.sleep(2000); // Wait for page to load
				return new ResponseFromPage(true, "Successfully clicked Submit button",
						"Submit button clicked");
			} else {
				return new ResponseFromPage(false, "Submit button is not clickable",
						"Failed to click Submit button");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to click Submit button: " + e.getMessage(),
					"Submit button click failed");
		}
	}

	/**
	 * Verify the new page URL contains the expected text
	 */
	public ResponseFromPage verifySuccessPageUrl() {
		try {
			String currentUrl = driver.getCurrentUrl();
			System.out.println("Current URL: " + currentUrl);
			if (currentUrl.contains("practicetestautomation.com/logged-in-successfully/")) {
				return new ResponseFromPage(true,
						"URL verification passed. Current URL: " + currentUrl,
						"New page URL contains 'practicetestautomation.com/logged-in-successfully/'");
			} else {
				return new ResponseFromPage(false,
						"URL verification failed. Expected URL to contain 'practicetestautomation.com/logged-in-successfully/' but got: " + currentUrl,
						"URL does not contain expected text");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to verify URL: " + e.getMessage(),
					"URL verification failed");
		}
	}

	/**
	 * Verify the success page contains expected congratulations text
	 */
	public ResponseFromPage verifySuccessPageText() {
		try {
			String pageText = driver.findElement(By.tagName("body")).getText();
			System.out.println("Page text: " + pageText);
			
			if (pageText.contains("Congratulations") || pageText.contains("successfully logged in")) {
				return new ResponseFromPage(true,
						"Success page text verification passed",
						"Page contains 'Congratulations' or 'successfully logged in'");
			} else {
				return new ResponseFromPage(false,
						"Success page does not contain expected text. Page text: " + pageText,
						"Expected success message not found on page");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to verify page text: " + e.getMessage(),
					"Page text verification failed");
		}
	}

	/**
	 * Verify that Log out button is displayed on the success page
	 */
	public ResponseFromPage verifyLogoutButtonDisplayed() {
		try {
			WebElement logoutButton = isPresent(logoutButtonBy);
			if (logoutButton != null && logoutButton.isDisplayed()) {
				return new ResponseFromPage(true,
						"Log out button is displayed on the page",
						"Log out button is visible on success page");
			} else {
				return new ResponseFromPage(false,
						"Log out button is not displayed on the page",
						"Log out button not found or not visible");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to verify Log out button: " + e.getMessage(),
					"Log out button verification failed");
		}
	}

	/**
	 * Verify error message is displayed
	 */
	public ResponseFromPage verifyErrorMessageDisplayed() {
		try {
			WebElement errorMessage = isPresent(errorMessageBy);
			if (errorMessage != null && errorMessage.isDisplayed()) {
				String errorText = errorMessage.getText();
				System.out.println("Error message: " + errorText);
				return new ResponseFromPage(true,
						"Error message is displayed: " + errorText,
						"Error message displayed on page");
			} else {
				return new ResponseFromPage(false,
						"Error message is not displayed",
						"Error message not found or not visible");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to verify error message: " + e.getMessage(),
					"Error message verification failed");
		}
	}

	/**
	 * Verify error message text matches expected value
	 */
	public ResponseFromPage verifyErrorMessageText(String expectedErrorText) {
		try {
			WebElement errorMessage = isPresent(errorMessageBy);
			if (errorMessage != null) {
				String actualErrorText = errorMessage.getText();
				System.out.println("Expected error: " + expectedErrorText);
				System.out.println("Actual error: " + actualErrorText);
				
				if (actualErrorText.contains(expectedErrorText)) {
					return new ResponseFromPage(true,
							"Error message text verification passed. Error: " + actualErrorText,
							"Error message text: '" + actualErrorText + "'");
				} else {
					return new ResponseFromPage(false,
							"Error message text mismatch. Expected: '" + expectedErrorText + "', Got: '" + actualErrorText + "'",
							"Error message does not match expected text");
				}
			} else {
				return new ResponseFromPage(false,
						"Error message element not found",
						"Error message not present on page");
			}
		} catch (Exception e) {
			return new ResponseFromPage(false, "Failed to verify error message text: " + e.getMessage(),
					"Error message text verification failed");
		}
	}
}

