package com.webDriver;

import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
public class DriverManager {
    // ThreadLocal ensures each thread has its own WebDriver instance
    private static ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    // Initialize driver based on browser name
    public static void setDriver(String selectedBrowser) throws Exception {
        WebDriver driver;
        switch (selectedBrowser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "chrome-docker":
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName("chrome");
                driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + selectedBrowser);
        }
        driver.manage().window().maximize();
        driverThread.set(driver);
    }

    // Get driver for current thread
    public static WebDriver getDriver() {
        return driverThread.get();
    }

    // Quit driver for current thread
    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            driver.quit();
            driverThread.remove();
        }
    }
}

