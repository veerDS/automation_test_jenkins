package com.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Generates summary.html and testcaseDetails.html after TestNG suite execution.
 * Adapted from the Cucumber ReportGenerator — Cucumber-specific APIs replaced
 * with plain Java equivalents compatible with TestNG's ITestResult data.
 */
public class ReportGenerator {

    private static final String OUTPUT_DIR      = "CustomReports/";
    private static final String DECIMAL_PATTERN = "##.##";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(DECIMAL_PATTERN);

    // Private constructor — static utility class
    private ReportGenerator() {}

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC ENTRY POINT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates both summary.html and testcaseDetails.html.
     * Called from TestListener.onFinish(ISuite).
     *
     * @param results   all collected TestResult entries for the suite
     * @param suiteName name of the TestNG suite (for report title)
     */
    public static void generateReports(List<TestResult> results, String suiteName) {
        ensureOutputDir();
        generateSummaryReport(results, suiteName);
        generateTestCaseDetailsReport(results, suiteName);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUMMARY REPORT
    // ─────────────────────────────────────────────────────────────────────────

    private static void generateSummaryReport(List<TestResult> results, String suiteName) {
        String title = suiteName + " — Execution Summary";
        String html  = buildSummaryHtml(results, title);
        writeToFile(html, "summary.html");
    }

    private static String buildSummaryHtml(List<TestResult> results, String title) {
        long passed  = countByStatus(results, TestResult.PASSED);
        long failed  = countByStatus(results, TestResult.FAILED);
        long skipped = countByStatus(results, TestResult.SKIPPED);
        long total   = passed + failed + skipped;

        double passPercent = total > 0 ? (100.0 * passed)  / total : 0;
        double failPercent = total > 0 ? (100.0 * failed)  / total : 0;
        double skipPercent = total > 0 ? (100.0 * skipped) / total : 0;

        String timestamp = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(new Date());
        String overallStatus = failed > 0 ? "FAIL" : "PASS";
        String overallColor  = failed > 0 ? "#e74c3c" : "#27ae60";

        return new HtmlBuilder()
                .doctype()
                .html("en")
                .head()
                .metaCharset("UTF-8")
                .meta("viewport", "width=device-width, initial-scale=1.0")
                .title(title)
                .style(summaryCSS())
                .closeTag() // </head>
                .body()
                .raw("<div class='header'><h1>🧪 " + escapeHtml(title) + "</h1></div>")
                .raw("<div class='meta-bar'>" +
                        "<span>🕐 Executed: " + timestamp + "</span>" +
                        "<span class='overall-badge' style='background:" + overallColor + "'>" +
                        "Overall: " + overallStatus + "</span></div>")

                // ── Stats cards ──────────────────────────────────────────────
                .raw("<div class='cards'>")
                .raw(card("Total",   String.valueOf(total),   "#2c3e50"))
                .raw(card("Passed",  String.valueOf(passed),  "#27ae60"))
                .raw(card("Failed",  String.valueOf(failed),  "#e74c3c"))
                .raw(card("Skipped", String.valueOf(skipped), "#f39c12"))
                .raw("</div>")

                // ── Summary table ────────────────────────────────────────────
                .h3("Execution Breakdown")
                .table("summary-table")
                .tr("header-row")
                .th("Status",     "th-status")
                .th("Count",      null)
                .th("Percentage", null)
                .closeTag() // </tr>
                .tr("row-pass")
                .td("✔ Passed",  "status-pass")
                .td(String.valueOf(passed),  null)
                .td(DECIMAL_FORMAT.format(passPercent)  + "%", null)
                .closeTag()
                .tr("row-fail")
                .td("✘ Failed",  "status-fail")
                .td(String.valueOf(failed),  null)
                .td(DECIMAL_FORMAT.format(failPercent)  + "%", null)
                .closeTag()
                .tr("row-skip")
                .td("⚠ Skipped", "status-skip")
                .td(String.valueOf(skipped), null)
                .td(DECIMAL_FORMAT.format(skipPercent)  + "%", null)
                .closeTag()
                .tr("row-total")
                .td("Total",     "status-total")
                .td(String.valueOf(total),   null)
                .td("100%",      null)
                .closeTag()
                .closeTag() // </table>

                .p("Note: Percentage values are rounded to two decimal places.")
                .closeAll()
                .toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TESTCASE DETAILS REPORT
    // ─────────────────────────────────────────────────────────────────────────

    private static void generateTestCaseDetailsReport(List<TestResult> results, String suiteName) {
        String title = suiteName + " — Test Case Details";
        String html  = buildDetailsHtml(results, title);
        writeToFile(html, "testcaseDetails.html");
    }

    private static String buildDetailsHtml(List<TestResult> results, String title) {
        HtmlBuilder builder = new HtmlBuilder()
                .doctype()
                .html("en")
                .head()
                .metaCharset("UTF-8")
                .meta("viewport", "width=device-width, initial-scale=1.0")
                .title(title)
                .style(detailsCSS())
                .closeTag() // </head>
                .body()
                .raw("<div class='header'><h1>📋 " + escapeHtml(title) + "</h1></div>")
                .table("details-table")
                .tr("header-row")
                .th("Sr. No",         null)
                .th("Test Case Name", null)
                .th("Status",         null)
                .th("Reason",         null)
                .closeTag(); // </tr> header

        int index = 1;
        for (TestResult result : results) {
            String statusClass;
            String statusIcon;
            switch (result.getStatus()) {
                case TestResult.PASSED:
                    statusClass = "status-pass";
                    statusIcon  = "✔ PASSED";
                    break;
                case TestResult.FAILED:
                    statusClass = "status-fail";
                    statusIcon  = "✘ FAILED";
                    break;
                default:
                    statusClass = "status-skip";
                    statusIcon  = "⚠ SKIPPED";
            }

            builder
                    .tr(index % 2 == 0 ? "row-even" : "row-odd")
                    .td(String.valueOf(index++), "sr-no")
                    .td(result.getTestCaseName(), null)
                    .raw("<td class='" + statusClass + "'>" + statusIcon + "</td>")
                    .td(result.getReason(), "reason-cell")
                    .closeTag(); // </tr>
        }

        builder
                .closeTag()  // </table>
                .closeAll(); // </body></html>

        return builder.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private static long countByStatus(List<TestResult> results, String status) {
        return results.stream()
                .filter(r -> status.equals(r.getStatus()))
                .count();
    }

    private static String card(String label, String value, String color) {
        return "<div class='card' style='border-top:4px solid " + color + "'>" +
                "<div class='card-value' style='color:" + color + "'>" + value + "</div>" +
                "<div class='card-label'>" + label + "</div></div>";
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static void ensureOutputDir() {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.out.println("[ReportGenerator] WARNING: Could not create output directory: " + OUTPUT_DIR);
        }
    }

    private static void writeToFile(String html, String fileName) {
        File outputFile = new File(OUTPUT_DIR + fileName);
        try {
            FileUtils.writeStringToFile(outputFile, html, StandardCharsets.UTF_8);
            System.out.println("[ReportGenerator] Report generated: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("[ReportGenerator] ERROR writing " + fileName + ": " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INLINE CSS  (replaces external CSS files from Cucumber version)
    // ─────────────────────────────────────────────────────────────────────────

    private static String summaryCSS() {
        return
                "* { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; }" +
                        "body { background: #f0f2f5; color: #333; }" +
                        ".header { background: #2c3e50; color: white; padding: 24px 32px; }" +
                        ".header h1 { font-size: 22px; font-weight: 600; }" +
                        ".meta-bar { display:flex; justify-content:space-between; align-items:center;" +
                        "            background:#ecf0f1; padding:10px 32px; font-size:13px; color:#555; }" +
                        ".overall-badge { color:white; padding:4px 14px; border-radius:12px; font-weight:bold; font-size:13px; }" +
                        ".cards { display:flex; gap:16px; padding:24px 32px; flex-wrap:wrap; }" +
                        ".card { background:white; border-radius:8px; padding:20px 28px; min-width:130px;" +
                        "        box-shadow:0 2px 6px rgba(0,0,0,0.08); text-align:center; }" +
                        ".card-value { font-size:36px; font-weight:700; }" +
                        ".card-label { font-size:13px; color:#888; margin-top:4px; text-transform:uppercase; letter-spacing:1px; }" +
                        "h3 { padding: 0 32px 12px; font-size:16px; color:#555; }" +
                        ".summary-table { width:50%; margin:0 32px 24px; border-collapse:collapse;" +
                        "                 box-shadow:0 2px 6px rgba(0,0,0,0.08); background:white; border-radius:8px; overflow:hidden; }" +
                        ".summary-table th, .summary-table td { padding:12px 20px; text-align:left; font-size:14px; }" +
                        ".header-row th { background:#2c3e50; color:white; }" +
                        ".row-pass td, .row-fail td, .row-skip td, .row-total td { border-bottom:1px solid #eee; }" +
                        ".status-pass  { color:#27ae60; font-weight:bold; }" +
                        ".status-fail  { color:#e74c3c; font-weight:bold; }" +
                        ".status-skip  { color:#f39c12; font-weight:bold; }" +
                        ".status-total { color:#2c3e50; font-weight:bold; }" +
                        ".row-total td { background:#f8f9fa; font-weight:bold; border-top:2px solid #dee2e6; }" +
                        "p { padding:0 32px 32px; font-size:12px; color:#999; }";
    }

    private static String detailsCSS() {
        return
                "* { box-sizing:border-box; margin:0; padding:0; font-family:'Segoe UI', Arial, sans-serif; }" +
                        "body { background:#f0f2f5; color:#333; }" +
                        ".header { background:#2c3e50; color:white; padding:24px 32px; }" +
                        ".header h1 { font-size:22px; font-weight:600; }" +
                        ".details-table { width:95%; margin:24px auto; border-collapse:collapse;" +
                        "                 background:white; border-radius:8px; overflow:hidden;" +
                        "                 box-shadow:0 2px 8px rgba(0,0,0,0.08); }" +
                        ".details-table th { background:#2c3e50; color:white; padding:14px 16px;" +
                        "                    text-align:left; font-size:14px; }" +
                        ".details-table td { padding:12px 16px; font-size:13px; border-bottom:1px solid #eee; vertical-align:top; }" +
                        ".row-even td { background:#f8f9fa; }" +
                        ".row-odd  td { background:#ffffff; }" +
                        ".sr-no { text-align:center; width:60px; color:#888; font-weight:bold; }" +
                        ".status-pass  { color:#27ae60; font-weight:bold; white-space:nowrap; }" +
                        ".status-fail  { color:#e74c3c; font-weight:bold; white-space:nowrap; }" +
                        ".status-skip  { color:#f39c12; font-weight:bold; white-space:nowrap; }" +
                        ".reason-cell  { color:#666; font-size:12px; max-width:400px; word-break:break-word; }";
    }

}