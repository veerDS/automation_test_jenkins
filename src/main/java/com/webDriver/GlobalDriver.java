package com.webDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.utils.LimitBrowser;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class GlobalDriver {
    private static ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();
    private static GlobalDriver globalDriver;
    private static WebDriver driver;
    private static RemoteWebDriver remoteWebDriver;
    private static Set<WebDriver> driverSet = new HashSet<WebDriver>();
    private static Set<LimitBrowser> limitBrowserSet = new HashSet<LimitBrowser>();



    private GlobalDriver(String selectedBrowser) throws Exception {
        switch (selectedBrowser) {
            case "chrome":
//		    ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless=new"); // modern headless mode
//            options.addArguments("--no-sandbox");
//            options.addArguments("--disable-dev-shm-usage");
//            driver = new ChromeDriver(options);
                driver = new ChromeDriver();
//            driver.manage().window().setSize(new Dimension(1920, 1080));
                driver.manage().window().maximize();
                break;
            case "edge":
                driver = new EdgeDriver();
                driver.manage().window().maximize();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                driver.manage().window().maximize();
                break;
            case "chrome-docker":
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName("chrome");
                URL url = new URL("http://localhost:4444/wd/hub");
                remoteWebDriver = new RemoteWebDriver(url, capabilities);
                driver = remoteWebDriver;
                driver.manage().window().maximize();
                break;
            default:
                throw new Exception("Browser not supported: " + selectedBrowser);
        }
    }

    public static GlobalDriver getGlobalDriver() {
        return globalDriver;
    }

    public static void setGlobalDriver(String selectedBrowser) throws Exception {
        globalDriver = new GlobalDriver(selectedBrowser);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public static void selectBrowser(String browserFromFeature, String parallelMode, String xmlTestName) throws Exception {
        boolean isPresent = false;

        if (parallelMode.equals("tests")) {
            for (LimitBrowser l : limitBrowserSet) {
                if (l.getTestName().equals(xmlTestName)) {
                    driver = l.getDriver();
                    isPresent = true;
                }
            }
            if (isPresent == false) {
                setGlobalDriver(browserFromFeature);
                driverSet.add(driver);
                limitBrowserSet.add(new LimitBrowser(xmlTestName, getGlobalDriver().getDriver()));
            }
        } else {
            setGlobalDriver(browserFromFeature);
            driverSet.add(driver);
        }
        System.out.println("driver is " + GlobalDriver.getGlobalDriver().getDriver().toString());
    }

    public static void qutitBrowser() {
        System.out.println("quit all the browsers.... ");
        for (WebDriver driver : driverSet)
            driver.quit();
    }
}
