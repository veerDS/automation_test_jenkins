package com.utils;

import static com.webDriver.GlobalDriver.qutitBrowser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class TestListener  implements ITestListener,ISuiteListener,IClassListener  {
	ExtentSparkReporter htmlReporter;
	ExtentReports reports;
	ExtentTest test;
	ExtentTest suiteTest;
	private static Map<String, ExtentTest> testMap = new HashMap<>();
	private static Map<String, ExtentTest> classMap = new HashMap<>();
	private static final String[] keys = new String[] {"test passed","test failed","skipped",
														"grandChildCount:","passGrandChild:","failGrandChild:",
														"skipGrandChild:"};
	private int totalTestCount,passedTestCount,failedTestCount,skippedTestCount;
	
	public void onStart(ISuite suite) {
		configureReport();
		suiteTest= reports.createTest(suite.getXmlSuite().getName());
		}
	public void onStart(ITestContext context) {
		System.out.println("On start method invoked....." + context.getName());
		ExtentTest testNode;
		String testName= context.getName();
        if (testMap.containsKey(testName)) {
        	testNode = testMap.get(testName);
        } else {
        	testNode = suiteTest.createNode(testName);
            testMap.put(testName, testNode);
        }
		}
	public void onBeforeClass(ITestClass testClass) {
		System.out.println(testClass.getRealClass().getSimpleName());
		System.out.println("i am test name "+testClass.getXmlTest().getName());
		ExtentTest classNode;
		String className= testClass.getXmlTest().getName()+testClass.getRealClass().getSimpleName();
        if (classMap.containsKey(className)) {
        	classNode = classMap.get(className);
        } else {
        	classNode = testMap.get(testClass.getXmlTest().getName()).createNode(testClass.getRealClass().getSimpleName());
            classMap.put(className, classNode);
        }
	}
	public void onTestStart(ITestResult result) {
		totalTestCount++;
	}
	public void onTestSuccess(ITestResult result) {
		String message = " ";
		System.out.println("Name of the test method successfully excuted " + result.getName());
		ExtentTest methodNode;
		String methodName= result.getName();
		String testClassName= result.getTestContext().getName()+result.getTestClass().getRealClass().getSimpleName();
		methodNode= classMap.get(testClassName).createNode(methodName);
		try {
			message = (String) result.getTestClass().getRealClass().getDeclaredField("log")
					.get(result.getInstance());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		reports.addTestRunnerOutput(message);
		methodNode.log(Status.PASS, message);
		passedTestCount++;
	}
	public void onTestFailure(ITestResult result) {
		WebDriver driver = null;
		String message = " ";
		ExtentTest methodNode;
		String methodName= result.getName();
		String testClassName= result.getTestContext().getName()+result.getTestClass().getRealClass().getSimpleName();
		methodNode= classMap.get(testClassName).createNode(methodName);
		try {
			driver = (WebDriver) result.getTestClass().getRealClass().getDeclaredField("driver")
					.get(result.getInstance());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Name of test method failed:" + result.getName());
		Date date = new Date();
		String timeStamp = date.toString().replace(":", "_").replace(" ", "_");
		String name = result.getTestContext().getName() + "_"+result.getName() +"_"+ timeStamp;
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String destPath = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + name + ".png";
		File destFile = new File(destPath);
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		try {
			message = (String) result.getTestClass().getRealClass().getDeclaredField("log")
					.get(result.getInstance());
		} catch (Exception e){
		System.out.println(e.getMessage());
		}
		reports.addTestRunnerOutput(message+"</br>"+result.getThrowable());
		methodNode.log(Status.FAIL, message+"</br>"+result.getThrowable(),MediaEntityBuilder.createScreenCaptureFromPath(destPath, name).build());
		failedTestCount++;
	}
	public void onTestSkipped(ITestResult result) {
		String message = " ";
		System.out.println("Name of test method skipped " + result.getName());
		ExtentTest methodNode;
		String methodName= result.getName();
		String testClassName= result.getTestContext().getName()+result.getTestClass().getRealClass().getSimpleName();
		methodNode= classMap.get(testClassName).createNode(methodName);		
		try {
			message = (String) result.getTestClass().getRealClass().getDeclaredField("log")
					.get(result.getInstance());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		reports.addTestRunnerOutput(message);
		methodNode.log(Status.SKIP, message);
		skippedTestCount++;
	}
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}
	public void onTestFailedWithTimeout(ITestResult result) {
		onTestFailure(result);
	}	
	public void onAfterClass(ITestClass testClass) {

	}
	public void onFinish(ITestContext context) {
		System.out.println("On Finished method invoked....."+ context.getName());
	}
	public void onFinish(ISuite suite) {
		reports.flush();
		qutitBrowser();
		boolean isContains=false;
		String path = System.getProperty("user.dir") + "/ExtentListenerReportDemo.html";
		try {
			List<String> allLines = Files.readAllLines(Paths.get(path));
			PrintWriter writer = new PrintWriter("ExtentReport.html");
			for (String line : allLines) {
				isContains=false;
				for(int i=0;i<keys.length;i++) {
					if(line.contains(keys[i])) {
						writer.println(addCounts(keys[i],line));
						isContains=true;
						break;
					}			
				}
				if(isContains==false) {
					writer.println(line);
				}
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
		htmlReporter.config().setReportName("This is my first Report");
		htmlReporter.config().setTheme(Theme.STANDARD);
		reports = new ExtentReports();
		reports.setAnalysisStrategy(AnalysisStrategy.SUITE);
		reports.setSystemInfo("Name","Rakesh");
		reports.setSystemInfo("Machine","Test");
		reports.setSystemInfo("OS","Windows11");
		reports.attachReporter(htmlReporter);
	}
	public String addCounts(String key, String line) {
		String val=line;
		boolean isSkip=false;
		switch(key) {
		case "test passed":
			val="<div><small data-tooltip='100%'><b>"+passedTestCount+"</b> test passed</small></div>";
			break;
		case "test failed":
			val="<small data-tooltip='0%'><b>"+failedTestCount+"</b> test failed,";
			isSkip=true;
			break;
		case "skipped":
			if(isSkip==true)
			val="<b>"+skippedTestCount+"</b> skipped, <b data-tooltip='%'>0</b> others";	
			break;
		case "grandChildCount:":
			val="grandChildCount: "+totalTestCount+",";
			break;
		case "passGrandChild:":
			val="passGrandChild: "+passedTestCount+",";
			break;
		case "failGrandChild:":
			val="failGrandChild: "+failedTestCount+",";
			break;
		case "skipGrandChild:":
			val="skipGrandChild: "+skippedTestCount+",";
			break;	
		}
		return val;
	}
}
