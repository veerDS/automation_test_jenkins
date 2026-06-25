package com.tests;

import static com.webDriver.GlobalDriver.getGlobalDriver;
import static com.webDriver.GlobalDriver.selectBrowser;
import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.utils.Print;
import com.utils.ResponseFromPage;

public class GoogleSearchTest extends Print {
    public WebDriver driver;
    private WebDriverWait wait;
    ResponseFromPage responseFromPage;
    public String log = "";
    
    @BeforeClass
    public void setup(ITestContext context) throws Exception {
        // Setup Chrome browser in non-headless mode
        selectBrowser("chrome", context.getSuite().getXmlSuite().getParallel().toString(), context.getName());
        driver = getGlobalDriver().getDriver();
        super.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @BeforeMethod
    public void refreshLog() {
        log = "";
    }
    
    @Test(priority = 1)
    public void testGoogleSearchWithAutosuggestions() throws Throwable {
        System.out.println("=== Starting Google Search Test ===");
        
        // Step 1: Open Google
        System.out.println("Step 1: Opening https://www.google.com/");
        try {
            driver.navigate().to("https://www.google.com/");
            Thread.sleep(2000);
            responseFromPage = new ResponseFromPage(true, "Successfully opened Google", "Navigated to Google homepage");
        } catch (Exception e) {
            responseFromPage = new ResponseFromPage(false, "Failed to open Google: " + e.getMessage(), "Google navigation failed");
        }
        log();
        
        // Step 2: Verify page title
        System.out.println("Step 2: Verifying page title");
        try {
            String pageTitle = driver.getTitle();
            System.out.println("Current page title: " + pageTitle);

            if (pageTitle.contains("Google")) {
                responseFromPage = new ResponseFromPage(true, "Page title verified: " + pageTitle, "Title validation passed");
            } else {
                responseFromPage = new ResponseFromPage(false, "Page title should contain 'Google' but got: " + pageTitle, "Title validation failed");
            }
        } catch (Exception e) {
            responseFromPage = new ResponseFromPage(false, "Failed to verify page title: " + e.getMessage(), "Title verification exception");
        }
        log();
        
        // Step 3: Enter "selenium" in Google search box
        System.out.println("Step 3: Entering 'selenium' in Google search box");
        try {
            WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
            searchBox.click();
            searchBox.sendKeys("selenium");
            Thread.sleep(2000);
            responseFromPage = new ResponseFromPage(true, "Successfully entered 'selenium' in search box", "Search input completed");
        } catch (Exception e) {
            responseFromPage = new ResponseFromPage(false, "Failed to enter 'selenium': " + e.getMessage(), "Search input failed");
        }
        log();
        
        // Step 4: Get the first 5 autosuggestion items
        System.out.println("Step 4: Getting first 5 autosuggestions");
        try {
            // Wait for suggestions dropdown to appear - using multiple possible XPath selectors
            List<WebElement> suggestions = null;
            try {
                // Try XPath for suggestion items in dropdown
                suggestions = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//li[@role='option']")
                    )
                );
            } catch (Exception e) {
                // Alternative XPath if first one doesn't work
                try {
                    suggestions = wait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//div[@role='option']")
                        )
                    );
                } catch (Exception e2) {
                    // Another alternative
                    suggestions = driver.findElements(By.xpath("//ul[@role='listbox']//li"));
                }
            }

            System.out.println("\n=== Autosuggestions List (First 5 items) ===");
            int count = Math.min(5, suggestions.size());

            StringBuilder suggestionsList = new StringBuilder();
            for (int i = 0; i < count; i++) {
                String suggestion = suggestions.get(i).getText();
                System.out.println((i + 1) + ". " + suggestion);
                suggestionsList.append((i + 1)).append(". ").append(suggestion).append(" | ");
            }

            if (count >= 1) {
                responseFromPage = new ResponseFromPage(true, "Retrieved " + count + " autosuggestions: " + suggestionsList,
                    "Autosuggestions: " + suggestionsList);
            } else {
                responseFromPage = new ResponseFromPage(false, "No autosuggestions found", "No suggestions available");
            }
        } catch (Exception e) {
            responseFromPage = new ResponseFromPage(false, "Failed to get autosuggestions: " + e.getMessage(), "Autosuggestion retrieval failed");
        }
        log();
        
        System.out.println("\n✓ Test completed successfully!");
    }
    
    public void log() {
        assertTrue(responseFromPage.isTrue(), responseFromPage.getMessage());
        log = logExtent(log, responseFromPage.getMessage());
    }
}


