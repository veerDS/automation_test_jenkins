package com.utils;

/**
 * Encapsulates the outcome of a single TestNG test method execution.
 * Stores the human-readable display name, status, and reason for the result.
 */
public class TestResult {

    // Status constants — mirrors ITestResult without importing TestNG into the POJO
    public static final String PASSED  = "PASSED";
    public static final String FAILED  = "FAILED";
    public static final String SKIPPED = "SKIPPED";

    private final String testCaseName;   // human-readable: from @Test(description) or method name
    private final String status;         // PASSED | FAILED | SKIPPED
    private final String reason;         // exception message, skip reason, or success note

    /**
     * @param testCaseName human-readable test name (from @Test description or method name)
     * @param status       one of TestResult.PASSED, FAILED, SKIPPED
     * @param reason       failure message, skip cause, or "Executed Successfully"
     */
    public TestResult(String testCaseName, String status, String reason) {
        this.testCaseName = testCaseName;
        this.status       = status;
        this.reason       = reason;
    }

    public String getTestCaseName() { return testCaseName; }
    public String getStatus()       { return status; }
    public String getReason()       { return reason; }
}