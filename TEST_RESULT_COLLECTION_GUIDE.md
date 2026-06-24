# Test Result Collection & Reporting Guide

## Overview

The `TestListener.java` has been enhanced with methods to collect and manage test results, similar to the `ExecutionReport` pattern from your previous Cucumber project. This guide explains the new functionality and how to use it.

## New Features

### 1. Test Result Collection

The TestListener now automatically collects test results into two categories:

- **Normal Results**: Regular test executions
- **Interframework Results**: Tests marked with `@InterframeworkJiraKey:` tag

#### How It Works

When each test finishes (success, failure, or skip), the listener:
1. Extracts JIRA tags from the test method
2. Classifies the test as normal or interframework
3. Captures test name, status, and error message
4. Stores the result in the appropriate collection

#### Supported Test Statuses

- `PASSED` - Test executed successfully
- `FAILED` - Test execution failed
- `SKIPPED` - Test was skipped

### 2. JIRA Tag Extraction

The `getJiraID()` method extracts JIRA identifiers from test metadata:

```java
// Supports tags like:
@SOL-1234          // Normal JIRA tag
@InterframeworkJiraKey:SOL-5678  // Interframework tag
```

### 3. Additional Test Information

Store supplementary information about tests for later analysis:

```java
// Usage in your test class:
TestListener listener = new TestListener();
listener.addAdditionalInfo("testPositiveLogIn", "Executed on Chrome browser");
```

This creates an `additionalInfo.txt` file in the `target/` directory with the following format:
```
testPositiveLogIn,Executed on Chrome browser
testNegativeLogIn,Executed on Chrome browser
```

### 4. TestResultInfo Inner Class

Each test result is stored as a `TestResultInfo` object containing:

```java
public class TestResultInfo {
    private final String jiraKey;      // JIRA identifier
    private final String testName;     // Test method name
    private final String status;       // PASSED, FAILED, SKIPPED
    private final String error;        // Error message (max 250 chars)
}
```

## Usage Examples

### Example 1: Access Collected Results

```java
TestListener listener = new TestListener();
// ... run tests ...

Map<String, List<?>> results = listener.getCollectedTestResults();
List<?> normalTests = results.get("normal");
List<?> interframeworkTests = results.get("interframework");

// Process results
for (Object result : normalTests) {
    TestListener.TestResultInfo info = (TestListener.TestResultInfo) result;
    System.out.println("Test: " + info.getTestName() + " Status: " + info.getStatus());
}
```

### Example 2: Add Additional Info During Test Execution

```java
public class MyTestClass extends Print {
    public WebDriver driver;
    public String log = "";
    
    @BeforeClass
    public void setup(ITestContext context) {
        // ... setup code ...
    }
    
    @Test
    public void testSomething() {
        // Test execution...
        
        // Add custom information
        TestListener listener = new TestListener();
        listener.addAdditionalInfo("testSomething", "Environment: QA, Browser: Chrome");
    }
}
```

### Example 3: Using JIRA Tags in Test Names

To leverage JIRA tag extraction, annotate your test methods:

```java
@Test
@Tags({@Tag("@SOL-1234"), @Tag("@positive-test")})
public void testPositiveLogIn() {
    // Test implementation
}
```

Note: The current implementation requires enhancement to read annotations from test methods. See **Customization** section below.

## Internal Data Flow

```
Test Execution (onTestSuccess/onTestFailure/onTestSkipped)
    ↓
collectTestResult() called with test status
    ↓
getJiraID() extracts JIRA tag
    ↓
convertStatusToString() converts status
    ↓
TestResultInfo object created
    ↓
Added to normalResults or interframeworkResults list
    ↓
Logged via SLF4J
```

## Thread Safety

The `addAdditionalInfo()` method is thread-safe using synchronized file access:

```java
private static final Object fileLock = new Object();

public void addAdditionalInfo(String testMethodName, String additionalInfo) {
    synchronized (fileLock) {
        // Thread-safe file write
    }
}
```

This ensures concurrent test execution doesn't cause file corruption.

## Logging

All operations are logged using SLF4J:

```
[INFO] Added normal test result: testPositiveLogIn - PASSED
[INFO] Added interframework test result: testNegativeLogIn - FAILED
[INFO] Additional info added to file successfully for test: testPositiveLogIn
[DEBUG] Scanning for JIRA tags in test: com.tests.PracticeTestLoginTest.testPositiveLogIn
```

## Customization

### Enhance JIRA Tag Extraction

To fully implement JIRA tag extraction from test method annotations, modify `getJiraID()`:

```java
private static String getJiraID(ITestResult result) {
    String jiraTag = "";
    try {
        Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();
        Tags tagsAnnotation = testMethod.getAnnotation(Tags.class);
        
        if (tagsAnnotation != null) {
            for (Tag tag : tagsAnnotation.value()) {
                String tagValue = tag.value();
                if (tagValue.startsWith("@SOL")) {
                    jiraTag = tagValue;
                    break;
                } else if (tagValue.startsWith("@InterframeworkJiraKey")) {
                    jiraTag = tagValue;
                }
            }
        }
    } catch (Exception e) {
        LOG.debug("Error extracting JIRA ID: {}", e.getMessage());
    }
    return jiraTag;
}
```

### Add Custom Result Filtering

```java
public List<TestListener.TestResultInfo> getFailedResults() {
    return normalResults.stream()
        .filter(r -> "FAILED".equals(r.getStatus()))
        .collect(Collectors.toList());
}
```

### Export Results to File

```java
public void exportResultsToCSV(String filePath) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
        writer.println("JIRA_KEY,TEST_NAME,STATUS,ERROR");
        
        for (TestListener.TestResultInfo result : normalResults) {
            writer.printf("%s,%s,%s,%s%n",
                result.getJiraKey(),
                result.getTestName(),
                result.getStatus(),
                result.getError());
        }
    }
}
```

## Files Generated

- `additionalInfo.txt`: Located in `target/` directory, contains supplementary test information
- `ExtentReport.html`: Main test report (unchanged)
- `FailedTestsScreenshots/`: Failed test screenshots (unchanged)

## Best Practices

1. **Call addAdditionalInfo() judiciously**: Only add relevant information to avoid bloating the file
2. **Format JIRA tags properly**: Use consistent naming conventions for easy parsing
3. **Clear results between test runs**: Call `clearCollectedResults()` before starting a new test suite
4. **Handle exceptions gracefully**: The listener logs errors without breaking test execution
5. **Use meaningful error messages**: Truncated to 250 chars but should be informative

## Migration from Cucumber Project

The implementation is adapted from your `ExecutionReport` class:

| Feature | Cucumber (ExecutionReport) | TestNG (TestListener) |
|---------|---------------------------|----------------------|
| Event handling | `TestCaseFinished` | `onTestSuccess/Failure/Skipped` |
| Result classification | JIRA tag prefix | JIRA tag prefix |
| Additional info | Scenario name + info | Test method name + info |
| Report generation | Delegated to `ReportGenerator` | Built into ExtentReports |
| Thread safety | `synchronized (fileLock)` | `synchronized (fileLock)` |

## Troubleshooting

**Issue**: `additionalInfo.txt` file not created
- **Solution**: Ensure `target/` directory exists; listener creates file on first write

**Issue**: JIRA tags not extracted
- **Solution**: Current implementation needs method annotation enhancement; see Customization section

**Issue**: File permission error when writing additional info
- **Solution**: Ensure test runner has write permissions to `target/` directory

**Issue**: Memory usage with many test results
- **Solution**: Call `clearCollectedResults()` to free memory between test suites

## API Reference

### Public Methods

```java
// Get collected test results
Map<String, List<?>> getCollectedTestResults()

// Add additional test information
void addAdditionalInfo(String testMethodName, String additionalInfo)

// Clear all collected results
void clearCollectedResults()
```

### Inner Class: TestResultInfo

```java
public static class TestResultInfo {
    String getJiraKey()
    String getTestName()
    String getStatus()
    String getError()
}
```

## See Also

- `AGENTS.md` - Project architecture and patterns
- Original `ExecutionReport.java` - Cucumber implementation reference
- `pom.xml` - Dependencies including Apache Commons Lang3 for StringUtils

