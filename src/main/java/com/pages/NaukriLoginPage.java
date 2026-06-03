package com.pages;

import com.controls.SeleniumControls;
import com.utils.Constants;
import com.utils.ResponseFromPage;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.time.Duration;

public class NaukriLoginPage extends SeleniumControls {
    WebDriver driver;
    WebDriverWait wait;
    String fixedXpathExample = "//div[@id='content']//a[text()='";

    By loginBtnBy = By.xpath("//a[@id='login_Layer']");
    By byUserName = By.xpath("//input[@id='usernameField']");
    By byPassword = By.xpath("//input[@id='passwordField']");
    By bySignIn = By.xpath("//button[text()='Login']");


    public NaukriLoginPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT));
        PageFactory.initElements(driver, this);
    }

    public By getByWithKey(String key) {
        By val = null;
        switch (key) {
            case "Login Button":
                val = loginBtnBy;
                break;
            case "Email Id":
                val = byUserName;
                break;
            case "Password":
                val = byPassword;
                break;
            case "Login":
                val = bySignIn;
                break;
        }
        return val;
    }
    public ResponseFromPage editResumeHeadline() throws InterruptedException {
        By byViewProfileBtn = By.xpath("//div[@class='view-profile-wrapper']/a");
        WebElement viewProfileBtn = wait.until(ExpectedConditions.elementToBeClickable(byViewProfileBtn));
        viewProfileBtn.click();
        Thread.sleep(2000);
        By byResumeHeadlineEditBtn = By.xpath("//span[text()='Resume headline']/following-sibling::span");
        WebElement editResumeHeadline = wait.until(ExpectedConditions.elementToBeClickable(byResumeHeadlineEditBtn));
        editResumeHeadline.click();

        WebElement resumeHeadlineTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resumeHeadlineTxt")));
        String resumeHeadlineText = resumeHeadlineTxt.getAttribute("value");
        System.out.println("Current resume headline: " + resumeHeadlineText);
        assert resumeHeadlineText != null;
        char lastChar = resumeHeadlineText.charAt(resumeHeadlineText.length() - 1);

        if (lastChar == '.') {
            resumeHeadlineText = resumeHeadlineText.substring(0, resumeHeadlineText.length() - 1);
            resumeHeadlineTxt.clear();
            resumeHeadlineTxt.sendKeys(resumeHeadlineText);
        } else {
            resumeHeadlineText = resumeHeadlineText.concat(".");
            resumeHeadlineTxt.clear();
            resumeHeadlineTxt.sendKeys(resumeHeadlineText);
        }

        wait.until(ExpectedConditions.attributeToBe(resumeHeadlineTxt, "value", resumeHeadlineText));
        Assert.assertEquals(resumeHeadlineText, resumeHeadlineTxt.getAttribute("value"));
        return new ResponseFromPage(true," Resume headline is edited successfully");
    }

    public ResponseFromPage saveProfile() {
        WebElement saveBtn = driver.findElement(By.xpath("//button[text()='Save']"));
        saveBtn.click();
        WebElement profileUpdateMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Profile updated successfully' and @class='success-text']")));
        //capture screenshot for profile updated successfully:
        try {
            //create screenshot dir under target:
            File screenshotDir = new File("target/screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdir();
            }
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileHandler.copy(src, new File("target" + File.separator + "screenshots" + File.separator + "profileUpdate.jpg"));
        } catch (Exception e) {
            System.out.println(e);
        }
        Assert.assertEquals(profileUpdateMsg.getText(), "Profile updated successfully");
        return new ResponseFromPage(true," Profile updated successfully");
    }
}
