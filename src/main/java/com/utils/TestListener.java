package com.utils;

import static com.webDriver.GlobalDriver.qutitBrowser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IClassListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class TestListener implements ITestListener, ISuiteListener, IClassListener {

    ExtentSparkReporter htmlReporter;
    ExtentReports reports;
    ExtentTest test;
    ExtentTest suiteTest;
    private static Map<String, ExtentTest> testMap = new HashMap<>();
    private static Map<String, ExtentTest> classMap = new HashMap<>();
    private static final String[] keys = new String[]{
            "test passed", "test failed", "skipped",
            "grandChildCount:", "passGrandChild:", "failGrandChild:", "skipGrandChild:","uri-anchor"
    };
    private int totalTestCount, passedTestCount, failedTestCount, skippedTestCount;

    // ── 1. ADD FIELD — result collector ─────────────────────────────────────────
    private final List<TestResult> suiteResults = new ArrayList<>();
    // ── ADD field alongside suiteResults ────────────────────────────────────────
    private String emailSummaryHtml = "";

    // ─── NEW HELPER ──────────────────────────────────────────────────────────────
    /**
     * Reads @Test(description="...") for a human-readable report node name.
     * Falls back to the method name if description is absent — nothing breaks.
     */
    private String getTestDisplayName(ITestResult result) {
        String description = result.getMethod().getDescription();
        return (description != null && !description.trim().isEmpty())
                ? description
                : result.getName();
    }
    // ─────────────────────────────────────────────────────────────────────────────

    // ─── ADD THIS HELPER METHOD ──────────────────────────────────────────────────
    /**
     * Reads @ModuleInfo(module="...") from the test class for a business-friendly
     * node name. Falls back to the simple class name if annotation is absent.
     */
    private String getClassDisplayName(Class<?> realClass) {
        ModuleInfo moduleInfo = realClass.getAnnotation(ModuleInfo.class);
        return (moduleInfo != null && !moduleInfo.module().trim().isEmpty())
                ? moduleInfo.module()
                : realClass.getSimpleName(); // graceful fallback — nothing breaks
    }
// ─────────────────────────────────────────────────────────────────────────────

    public void onStart(ISuite suite) {
        configureReport();
        suiteTest = reports.createTest(suite.getXmlSuite().getName());
    }

    public void onStart(ITestContext context) {
        System.out.println("On start method invoked....." + context.getName());
        ExtentTest testNode;
        String testName = context.getName();
        if (testMap.containsKey(testName)) {
            testNode = testMap.get(testName);
        } else {
            testNode = suiteTest.createNode(testName);
            testMap.put(testName, testNode);
        }
    }

    public void onBeforeClass(ITestClass testClass) {
        System.out.println(testClass.getRealClass().getSimpleName());
        System.out.println("i am test name " + testClass.getXmlTest().getName());
        ExtentTest classNode;
        String className = testClass.getXmlTest().getName() + testClass.getRealClass().getSimpleName();
        if (classMap.containsKey(className)) {
            classNode = classMap.get(className);
        } else {
            String displayName = getClassDisplayName(testClass.getRealClass()); // ← NEW: human-readable label
            classNode = testMap.get(testClass.getXmlTest().getName())
                    .createNode(displayName);
            classMap.put(className, classNode);
        }
    }

    public void onTestStart(ITestResult result) {
        totalTestCount++;
    }

    public void onTestSuccess(ITestResult result) {
        suiteResults.add(new TestResult(getTestDisplayName(result), TestResult.PASSED, "Executed Successfully"));
        String message = " ";
        System.out.println("Name of the test method successfully executed: " + result.getName());
        String displayName = getTestDisplayName(result);                          // ← CHANGED
        String testClassName = result.getTestContext().getName()
                + result.getTestClass().getRealClass().getSimpleName();
        ExtentTest methodNode = classMap.get(testClassName).createNode(displayName); // ← CHANGED
        try {
            message = (String) result.getTestClass().getRealClass()
                    .getDeclaredField("log").get(result.getInstance());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        reports.addTestRunnerOutput(message);
        methodNode.log(Status.PASS, message);
        passedTestCount++;
    }

    public void onTestFailure(ITestResult result) {
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Unknown failure";
        suiteResults.add(new TestResult(getTestDisplayName(result), TestResult.FAILED, reason));
        WebDriver driver = null;
        String message = " ";
        String displayName = getTestDisplayName(result);                          // ← CHANGED
        String testClassName = result.getTestContext().getName()
                + result.getTestClass().getRealClass().getSimpleName();
        ExtentTest methodNode = classMap.get(testClassName).createNode(displayName); // ← CHANGED
        try {
            driver = (WebDriver) result.getTestClass().getRealClass()
                    .getDeclaredField("driver").get(result.getInstance());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Name of test method failed: " + result.getName());
        Date date = new Date();
        String timeStamp = date.toString().replace(":", "_").replace(" ", "_");
        // ↓ Intentionally keep result.getName() here — file names must not have spaces
        String name = result.getTestContext().getName() + "_" + result.getName() + "_" + timeStamp;
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destPath = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + name + ".png";
        // ── RELATIVE PATH — for embedding in the HTML report ────────────────────
        String destRelativePath = "FailedTestsScreenshots/" + name + ".png";

        File destFile = new File(destPath);
        try {
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            message = (String) result.getTestClass().getRealClass()
                    .getDeclaredField("log").get(result.getInstance());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        reports.addTestRunnerOutput(message + "</br>" + result.getThrowable());
        methodNode.log(Status.FAIL, message + "</br>" + result.getThrowable(),
                MediaEntityBuilder.createScreenCaptureFromPath(destRelativePath, name).build());
        failedTestCount++;
    }

    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Test was skipped";
        suiteResults.add(new TestResult(getTestDisplayName(result), TestResult.SKIPPED, reason));
        String message = " ";
        System.out.println("Name of test method skipped: " + result.getName());
        String displayName = getTestDisplayName(result);                          // ← CHANGED
        String testClassName = result.getTestContext().getName()
                + result.getTestClass().getRealClass().getSimpleName();
        ExtentTest methodNode = classMap.get(testClassName).createNode(displayName); // ← CHANGED
        try {
            message = (String) result.getTestClass().getRealClass()
                    .getDeclaredField("log").get(result.getInstance());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        reports.addTestRunnerOutput(message);
        methodNode.log(Status.SKIP, message);
        skippedTestCount++;
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }

    public void onAfterClass(ITestClass testClass) {}

    public void onFinish(ITestContext context) {
        System.out.println("On Finished method invoked....." + context.getName());
    }

    public void onFinish(ISuite suite) {
        reports.flush();
        qutitBrowser();
        ReportGenerator.generateReports(suiteResults, suite.getXmlSuite().getName());
        // ↓ NEW — generate email-safe summary and write to file for index.js to read
        emailSummaryHtml = ReportGenerator.generateEmailSummary(suiteResults);
        writeEmailSummary(emailSummaryHtml);
        boolean isContains = false;
        String path = System.getProperty("user.dir") + "/ExtentListenerReportDemo.html";
        try {
            List<String> allLines = Files.readAllLines(Paths.get(path));
            PrintWriter writer = new PrintWriter("ExtentReport.html");
            for (String line : allLines) {
                isContains = false;
                for (int i = 0; i < keys.length; i++) {
                    if (line.contains(keys[i])) {
                        writer.println(addCounts(keys[i], line));
                        isContains = true;
                        break;
                    }
                }
                if (!isContains) writer.println(line);
            }
            writer.close();
            Thread.sleep(1000);
            Files.delete(Paths.get(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void configureReport() {
        htmlReporter = new ExtentSparkReporter("ExtentListenerReportDemo.html");
        htmlReporter.config().setDocumentTitle("Extent Listener Report Demo");
        htmlReporter.config().setReportName("Automation Test Results");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setCss(".uri-anchor { display: none !important; }");
        htmlReporter.config().setCss(".info .badge-default ~ .m-l-5 { display: none !important; }");
        reports = new ExtentReports();
        reports.setAnalysisStrategy(AnalysisStrategy.SUITE);
        reports.setSystemInfo("Name", "Automation QA");
        reports.setSystemInfo("Machine", "Test-001");
        reports.setSystemInfo("OS", "Windows11");
        reports.attachReporter(htmlReporter);
    }

    public String addCounts(String key, String line) {
        String val = line;
        boolean isSkip = false;
        switch (key) {
            case "test passed":
                val = "<div><small data-tooltip='100%'><b>" + passedTestCount + "</b> test passed</small></div>";
                break;
            case "test failed":
                val = "<small data-tooltip='0%'><b>" + failedTestCount + "</b> test failed,";
                isSkip = true;
                break;
            case "skipped":
                if (isSkip)
                    val = "<b>" + skippedTestCount + "</b> skipped, <b data-tooltip='%'>0</b> others";
                break;
            case "grandChildCount:":
                val = "grandChildCount: " + totalTestCount + ",";
                break;
            case "passGrandChild:":
                val = "passGrandChild: " + passedTestCount + ",";
                break;
            case "failGrandChild:":
                val = "failGrandChild: " + failedTestCount + ",";
                break;
            case "skipGrandChild:":
                val = "skipGrandChild: " + skippedTestCount + ",";
                break;
            case "uri-anchor":
                val = "";
                break;
        }
        return val;
    }
    /**
     * Writes the email-safe HTML snippet to CustomReports/emailSummary.html
     * GitHub Actions index.js reads this file and injects it into the email body.
     */
    private void writeEmailSummary(String html) {
        File outputFile = new File("CustomReports/emailSummary.html");
        try {
            FileUtils.writeStringToFile(outputFile, html, StandardCharsets.UTF_8);
            System.out.println("[TestListener] Email summary written: "
                    + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("[TestListener] ERROR writing emailSummary.html: "
                    + e.getMessage());
        }
    }
}