package com.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Constants;
import com.utils.ResponseFromPage;

public class GoogleSearchPage extends PageTemplate {
    WebDriver driver;
    WebDriverWait wait;
    
    // Locators for Google Search elements
    By searchBoxBy = By.name("q");
    By suggestionsBy = By.xpath("//li[@role='option']");
    By altSuggestionsBy = By.xpath("//div[@role='option']");
    
    public GoogleSearchPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT));
        PageFactory.initElements(driver, this);
    }
    
    public By getByWithKey(String key) {
        By val = null;
        switch (key) {
            case "Search Box":
                val = searchBoxBy;
                break;
            case "Suggestions":
                val = suggestionsBy;
                break;
            case "Alt Suggestions":
                val = altSuggestionsBy;
                break;
        }
        return val;
    }
    
    public ResponseFromPage getFirstFiveAutosuggestions() {
        try {
            Thread.sleep(1000);
            
            // Get suggestions elements using isPresent method from SeleniumControls
            WebElement suggestionElement = isPresent(suggestionsBy);
            List<WebElement> suggestions = null;
            
            if (suggestionElement != null) {
                suggestions = driver.findElements(suggestionsBy);
            } else {
                // Try alternative selector
                suggestionElement = isPresent(altSuggestionsBy);
                if (suggestionElement != null) {
                    suggestions = driver.findElements(altSuggestionsBy);
                }
            }
            
            if (suggestions != null && !suggestions.isEmpty()) {
                int count = Math.min(5, suggestions.size());
                StringBuilder suggestionsList = new StringBuilder();
                
                for (int i = 0; i < count; i++) {
                    String suggestion = suggestions.get(i).getText();
                    suggestionsList.append((i + 1)).append(". ").append(suggestion).append(" | ");
                }
                
                return new ResponseFromPage(true, "Retrieved " + count + " autosuggestions: " + suggestionsList, 
                    "Autosuggestions: " + suggestionsList);
            } else {
                return new ResponseFromPage(false, "No autosuggestions found", "No suggestions available");
            }
        } catch (Exception e) {
            return new ResponseFromPage(false, "Failed to get autosuggestions: " + e.getMessage(), "Autosuggestion retrieval failed");
        }
    }
}

