AutoTestFramework
# AGENTS.md - AI Coding Agent Guide

This is a Selenium-based test automation project for Jenkins CI/CD integration, using TestNG and ExtentReports.

## Project Architecture

### Core Components

1. **WebDriver Management** (`src/main/java/com/webDriver/GlobalDriver.java`)
    - Singleton pattern: manages browser instances via static `GlobalDriver` class
    - Supports parallel execution: `selectBrowser()` handles parallel test mode by maintaining separate driver instances per test
    - Browser support: Chrome (maximized, non-headless by default), Edge, Firefox
    - Critical: Headless mode is commented out in lines 23-26; uncomment to enable
    - Cleanup: `quitBrowser()` iterates through `driverSet` to close all browser instances

2. **Page Object Model (POM)** (`src/main/java/com/pages/`)
    - Base: `PageTemplate` extends `SeleniumControls` - all page classes inherit this
    - Pattern: Each page (e.g., `BoxAppLoginPage`, `GoogleSearchPage`) wraps application pages
    - Response wrapper: All interactions return `ResponseFromPage` objects (boolean status + message)
    - Examples in codebase: `BoxAppLoginPage`, `UnicornSignupSelfPage`, `GoogleSearchPage`

3. **Selenium Controls Layer** (`src/main/java/com/controls/SeleniumControls.java`)
    - Wrapper methods around Selenium WebDriver API
    - Includes: click, sendKeys, wait conditions, navigation, alerts, window/frame switching
    - Uses `WebDriverWait` with `Constants.MAX_WAIT_TIME_TO_FIND_ELEMENT` (typically 10-15 seconds)
    - Extends `Print` base class for logging

4. **Utilities** (`src/main/java/com/utils/`)
    - `ResponseFromPage`: POJO wrapper for test step responses (isTrue, message, extentMessage); supports dual constructors with/without extentMessage
    - `Print`: Base class with `logExtent()` for report logging (concatenates messages with `<br />`)
    - `TestListener`: Implements TestNG listeners (ITestListener, ISuiteListener, IClassListener) for ExtentReports integration
    - `LimitBrowser`: Manages per-test browser instances for parallel execution (maps testName to WebDriver)
    - `DataAccess`: TestNG @DataProvider utilities for Excel-based test data via `@DataProvider` annotations
    - `ExcelAccess`: Utility for reading Excel files (getRowCount, getCellCount, etc.)
    - `Constants`: Configuration values (MAX_WAIT_TIME_TO_FIND_ELEMENT=60 seconds, app URLs)

### Test Execution Flow

1. Tests configured in `testng.xml` with parallel execution settings (10 thread pool, `parallel="tests"`)
2. Maven-surefire plugin runs tests via `mvn test` (uses testng.xml suite)
3. `TestListener` captures test results and creates `ExtentReport.html` with hierarchy:
    - Suite → Test (from XML) → Class (test class) → Method (test method)
4. Failed tests trigger screenshot capture in `FailedTestsScreenshots/` directory
5. Extent reports include: test status, logs from `log` field (concatenated HTML), screenshots

## Key Patterns & Conventions

### Test Class Structure
- Extend `Print` class for logging capability
- Declare: `public WebDriver driver;` (populated by `@BeforeClass`)
- Declare: `public String log = "";` (for ExtentReports)
- Use `@BeforeClass` with `ITestContext` parameter to initialize browser via `selectBrowser(ITestContext)`
    - Call: `selectBrowser("chrome", context.getSuite().getXmlSuite().getParallel().toString(), context.getName())`
    - Retrieve driver: `driver = getGlobalDriver().getDriver();`
- Use `@BeforeMethod` to reset `log = ""` between tests
- **Pattern variant**: Add helper method `log()` to combine assertion and logging (see PracticeTestLoginTest.java lines 198-201)
  ```java
  private void log() {
      assertTrue(responseFromPage.isTrue(), responseFromPage.getMessage());
      log = logExtent(log, responseFromPage.getMessage());
  }
  ```

### Response-Based Assertions
- Every action returns `ResponseFromPage`: `new ResponseFromPage(boolean isTrue, String message)`
- Assert with: `assertTrue(responseFromPage.isTrue(), responseFromPage.getMessage())`
- Append to log: `log = logExtent(log, responseFromPage.getMessage());`
- Pattern (from GoogleSearchTest.java lines 63-67):
  ```java
  responseFromPage = new ResponseFromPage(success, "message");
  assertTrue(responseFromPage.isTrue(), responseFromPage.getMessage());
  log = logExtent(log, responseFromPage.getMessage());
  ```

### Wait & Element Handling
- Use `WebDriverWait` with `ExpectedConditions` (not sleeps)
- Common patterns: `titleIs()`, `urlToBe()`, `presenceOfElementLocated()`, `elementToBeClickable()`
- Locators: By.name(), By.xpath(), By.id(), etc. - passed as `By` objects
- Multiple XPath fallbacks recommended (see GoogleSearchTest.java lines 78-92)

### Logging Pattern
- Test class declares: `public String log = "";`
- Every step uses: `responseFromPage = new ResponseFromPage(...);` then `log = logExtent(log, ...);`
- TestListener extracts `log` field via reflection (line 85-86 in TestListener.java)

## Developer Workflows

### Running Tests
- **Single test**: `mvn test` (runs testng.xml)
- **Specific test class**: Edit testng.xml to uncomment class, then `mvn test`
- **Parallel execution**: Already configured in testng.xml (10 threads, "tests" mode)
- **Clean rebuild**: `mvn clean install` then `mvn test`
- **Java version**: Project requires Java 21 (see pom.xml property `maven.compiler.target`)

### Working with Test Data
- Test data stored in `TestData/` directory as Excel files (`.xlsx`)
- Use `@DataProvider` annotation from `DataAccess` class to parameterize tests
- Example: `@DataProvider(name="unicornSignup")` reads from `TestData/unicornSignup.xlsx`
- See `DataAccess.java` for provider definitions and `ExcelAccess.java` for Excel utility methods

### CI/CD Integration (Jenkins)
- Project designed for Jenkins: testng.xml configured for parallel execution
- Reports generated: `ExtentReport.html` in project root
- Screenshots stored: `FailedTestsScreenshots/` (timestamp-based naming)
- Jenkins can parse test results from maven-surefire output

### Debugging Failed Tests
1. Check `FailedTestsScreenshots/` for browser state at failure
2. Review `ExtentReport.html` for detailed logs and stack traces
3. Check console output (System.out.println) for intermediate steps
4. Headless mode: Uncomment in GlobalDriver.java line 24 to debug rendering issues

## Critical Developer Notes

### Browser Initialization
- **DO NOT forget `selectBrowser()` in `@BeforeClass`** - initializes static `GlobalDriver`
- Parallel mode: Detects test name via `ITestContext.getName()` and reuses driver if already created for that test
- Window size: Hardcoded to maximized (not 1920x1080) - change in GlobalDriver.java line 30

### Test Cleanup
- `quitBrowser()` called automatically in TestListener.onFinish() (line 160)
- Manual cleanup rarely needed (suite listener handles it)

### Common Pitfalls
1. **Forgetting to inherit `Print`**: Breaks logging. All test classes must extend `Print`.
2. **Direct assertions instead of ResponseFromPage**: TestListener won't capture failures properly.
3. **Hardcoded waits (Thread.sleep)**: Use `WebDriverWait` instead for reliability.
4. **Extent report not updating**: Ensure test class has `public String log = "";` field.
5. **Screenshots in non-headless mode**: If running on server without display, enable headless mode (GlobalDriver.java line 24).
6. **Method name typo in GlobalDriver**: Note that `quitBrowser()` is misspelled as `qutitBrowser()` in the actual implementation (GlobalDriver.java line 72); calls from TestListener use the same spelling.
7. **Not passing correct parallel mode string**: In `@BeforeClass`, ensure you pass `context.getSuite().getXmlSuite().getParallel().toString()` to `selectBrowser()` to enable proper parallel execution detection.

## File Structure Reference
```
src/main/java/com/
├── webDriver/GlobalDriver.java          → Browser singleton manager
├── pages/PageTemplate.java              → Base POM class (all pages inherit)
├── controls/SeleniumControls.java       → Selenium wrapper methods
└── utils/
    ├── Print.java                       → Logging base class
    ├── ResponseFromPage.java            → Response wrapper POJO
    ├── TestListener.java                → ExtentReports listener
    ├── DataAccess.java                  → TestNG DataProvider utilities
    ├── ExcelAccess.java                 → Excel file reading utilities
    ├── LimitBrowser.java                → Parallel browser instance manager
    └── Constants.java                   → Configuration values

src/test/java/com/
├── tests/                               → Test classes (extends Print)
│   ├── PracticeTestLoginTest.java (example)
│   ├── BoxAppLoginLogOut.java
│   ├── UnicornSignUp.java
│   └── ... (other test classes)
└── demo/                                → Demo and learning resources
    ├── Automation.java
    ├── SampleTest.java
    └── ResponseFromPage.java (reference copy)
```

## Related Files
- `testng.xml`: Test suite configuration (parallel mode, browser parameter, test class list)
- `pom.xml`: Maven dependencies including:
    - Selenium 4.18.1, TestNG 7.9.0, ExtentReports 5.1.1, ExtentReports TestNG adapter 1.2.0
    - WebDriverManager 5.4.1 (auto browser driver management)
    - Apache POI 5.2.3 (Excel file reading for test data)
    - PDFBox 2.0.24 (PDF handling)
    - Java compiler target: 21
- `ExtentReport.html`: Generated test report (created by TestListener)
- `FailedTestsScreenshots/`: Auto-created directory for failure screenshots
- `TestData/`: Directory for Excel files (.xlsx) used by DataProvider in tests

