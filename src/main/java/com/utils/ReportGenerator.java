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

    /**
     * Generates an email-safe HTML summary snippet.
     * Uses inline SVG for the pie chart — Canvas/JS are blocked by all email clients.
     * Called by the GitHub Actions index.js after test execution completes.
     *
     * @param results   all collected TestResult entries
     * @return          self-contained HTML string safe for email body embedding
     */
    public static String generateEmailSummary(List<TestResult> results) {
        long passed  = countByStatus(results, TestResult.PASSED);
        long failed  = countByStatus(results, TestResult.FAILED);
        long skipped = countByStatus(results, TestResult.SKIPPED);
        long total   = passed + failed + skipped;

        double passPercent = total > 0 ? (100.0 * passed)  / total : 0;
        double failPercent = total > 0 ? (100.0 * failed)  / total : 0;
        double skipPercent = total > 0 ? (100.0 * skipped) / total : 0;

        String overallColor = failed > 0 ? "#e74c3c" : "#27ae60";
        String overallText  = failed > 0 ? "FAIL"    : "PASS";
        String timestamp    = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(new Date());

        String svgChart = buildSvgPieChart(passed, failed, skipped, total);

        // All styles are INLINE — email clients strip <style> blocks
        return
                // ── Header ──────────────────────────────────────────────────────────
                "<div style='font-family:Segoe UI,Arial,sans-serif;max-width:700px;margin:0 auto'>" +
                        "<div style='background:#2c3e50;color:white;padding:18px 24px;border-radius:8px 8px 0 0'>" +
                        "<h2 style='margin:0;font-size:18px;font-weight:500'>Automation Test Suite — Execution Summary</h2>" +
                        "<p style='margin:6px 0 0;font-size:12px;color:#bdc3c7'>Executed: " + timestamp + "</p>" +
                        "</div>" +

                        // ── Overall badge ────────────────────────────────────────────────────
                        "<div style='background:#ecf0f1;padding:8px 24px;display:flex;justify-content:space-between;align-items:center'>" +
                        "<span style='font-size:12px;color:#555'>Automated test execution completed</span>" +
                        "<span style='background:" + overallColor + ";color:white;padding:3px 12px;" +
                        "border-radius:10px;font-size:12px;font-weight:500'>Overall: " + overallText + "</span>" +
                        "</div>" +

                        // ── Stat cards (table-based for email client compatibility) ──────────
                        "<table width='100%' cellpadding='0' cellspacing='8' " +
                        "style='padding:16px 24px;background:#f0f2f5'>" +
                        "<tr>" +
                        emailStatCard("Total",   String.valueOf(total),   "#2c3e50") +
                        emailStatCard("Passed",  String.valueOf(passed),  "#27ae60") +
                        emailStatCard("Failed",  String.valueOf(failed),  "#e74c3c") +
                        emailStatCard("Skipped", String.valueOf(skipped), "#f39c12") +
                        "</tr></table>" +

                        // ── Chart + Table side by side (table layout — email safe) ───────────
                        "<table width='100%' cellpadding='0' cellspacing='0' " +
                        "style='padding:0 24px 16px;background:#f0f2f5'>" +
                        "<tr valign='top'>" +

                        // SVG chart cell
                        "<td width='240' style='padding-right:16px'>" +
                        "<div style='background:white;border-radius:8px;padding:16px;text-align:center'>" +
                        "<p style='margin:0 0 10px;font-size:12px;color:#555;font-weight:500'>Execution distribution</p>" +
                        svgChart +
                        emailChartLegend(passed, failed, skipped, total) +
                        "</div></td>" +

                        // Summary table cell
                        "<td style='vertical-align:top'>" +
                        "<div style='background:white;border-radius:8px;overflow:hidden'>" +
                        "<table width='100%' cellpadding='10' cellspacing='0' style='border-collapse:collapse;font-size:13px'>" +
                        "<tr style='background:#2c3e50'>" +
                        "<th style='color:white;text-align:left;padding:10px 14px;font-weight:500'>Status</th>" +
                        "<th style='color:white;text-align:left;padding:10px 14px;font-weight:500'>Count</th>" +
                        "<th style='color:white;text-align:left;padding:10px 14px;font-weight:500'>Percentage</th>" +
                        "</tr>" +
                        emailTableRow("✔ Passed",  String.valueOf(passed),  DECIMAL_FORMAT.format(passPercent) + "%", "#27ae60", "#f9fff9") +
                        emailTableRow("✘ Failed",  String.valueOf(failed),  DECIMAL_FORMAT.format(failPercent) + "%", "#e74c3c", "#fff9f9") +
                        emailTableRow("⚠ Skipped", String.valueOf(skipped), DECIMAL_FORMAT.format(skipPercent) + "%", "#f39c12", "#fffdf5") +
                        "<tr style='background:#f8f9fa;border-top:2px solid #dee2e6'>" +
                        "<td style='padding:10px 14px;font-weight:500;color:#2c3e50'>Total</td>" +
                        "<td style='padding:10px 14px;font-weight:500'>" + total + "</td>" +
                        "<td style='padding:10px 14px;font-weight:500'>100%</td>" +
                        "</tr>" +
                        "</table></div></td>" +

                        "</tr></table>" +

                        "<p style='padding:0 24px 16px;font-size:11px;color:#aaa;background:#f0f2f5;margin:0'>" +
                        "Note: Percentage values are rounded to two decimal places.</p>" +
                        "</div>";
    }

// ── Email-specific helper methods ─────────────────────────────────────────────

    /**
     * Table-based stat card — div/flex don't work reliably in Outlook.
     */
    private static String emailStatCard(String label, String value, String color) {
        return "<td style='width:25%;padding:4px'>" +
                "<div style='background:white;border-radius:6px;padding:14px;" +
                "text-align:center;border-top:3px solid " + color + "'>" +
                "<div style='font-size:26px;font-weight:500;color:" + color + "'>" + value + "</div>" +
                "<div style='font-size:10px;color:#888;margin-top:4px;text-transform:uppercase;" +
                "letter-spacing:1px'>" + label + "</div>" +
                "</div></td>";
    }

    private static String emailTableRow(String status, String count, String pct,
                                        String color, String rowBg) {
        return "<tr style='background:" + rowBg + ";border-bottom:1px solid #f0f0f0'>" +
                "<td style='padding:10px 14px;color:" + color + ";font-weight:500'>" + status + "</td>" +
                "<td style='padding:10px 14px'>" + count + "</td>" +
                "<td style='padding:10px 14px'>" + pct + "</td>" +
                "</tr>";
    }

    private static String emailChartLegend(long passed, long failed, long skipped, long total) {
        return "<div style='margin-top:10px;font-size:11px;color:#555;text-align:left;padding:0 4px'>" +
                emailLegendRow("#27ae60", formatLabel("Passed",  passed,  total)) +
                emailLegendRow("#e74c3c", formatLabel("Failed",  failed,  total)) +
                emailLegendRow("#f39c12", formatLabel("Skipped", skipped, total)) +
                "</div>";
    }

    private static String emailLegendRow(String color, String label) {
        return "<div style='display:inline-block;margin:3px 6px'>" +
                "<span style='display:inline-block;width:10px;height:10px;border-radius:50%;" +
                "background:" + color + ";margin-right:4px;vertical-align:middle'></span>" +
                "<span style='vertical-align:middle'>" + escapeHtml(label) + "</span>" +
                "</div>";
    }

    /**
     * Builds a pure SVG doughnut chart — renders in all email clients without JS.
     * Segments computed using SVG stroke-dasharray on a circle.
     */
    private static String buildSvgPieChart(long passed, long failed, long skipped, long total) {
        if (total == 0) {
            return "<svg width='180' height='180' viewBox='0 0 180 180' " +
                    "xmlns='http://www.w3.org/2000/svg'>" +
                    "<circle cx='90' cy='90' r='70' fill='none' stroke='#eee' stroke-width='28'/>" +
                    "<text x='90' y='94' text-anchor='middle' font-size='13' fill='#aaa'>No data</text>" +
                    "</svg>";
        }

        // SVG doughnut uses stroke-dasharray technique on a circle
        // circumference = 2 * PI * r = 2 * 3.14159 * 62 ≈ 389.6
        double r   = 62.0;
        double cx  = 90.0;
        double cy  = 90.0;
        double circ = 2 * Math.PI * r; // full circumference

        double passDash  = (passed  / (double) total) * circ;
        double failDash  = (failed  / (double) total) * circ;
        double skipDash  = (skipped / (double) total) * circ;

        // Each segment: stroke-dasharray="segmentLength circumference"
        // stroke-dashoffset rotates the start point of each segment
        double passOffset = circ * 0.25;                        // start from top (12 o'clock)
        double failOffset = passOffset - passDash;
        double skipOffset = failOffset - failDash;

        DecimalFormat df = new DecimalFormat("##.##");

        return "<svg width='180' height='180' viewBox='0 0 180 180' " +
                "xmlns='http://www.w3.org/2000/svg'>" +

                // Background track
                "<circle cx='" + cx + "' cy='" + cy + "' r='" + r + "' " +
                "fill='none' stroke='#f0f0f0' stroke-width='28'/>" +

                // Passed segment
                (passed > 0 ?
                        "<circle cx='" + cx + "' cy='" + cy + "' r='" + r + "' " +
                                "fill='none' stroke='#27ae60' stroke-width='28' " +
                                "stroke-dasharray='" + df.format(passDash) + " " + df.format(circ) + "' " +
                                "stroke-dashoffset='" + df.format(passOffset) + "' " +
                                "transform='rotate(-90 " + cx + " " + cy + ")'/>" : "") +

                // Failed segment
                (failed > 0 ?
                        "<circle cx='" + cx + "' cy='" + cy + "' r='" + r + "' " +
                                "fill='none' stroke='#e74c3c' stroke-width='28' " +
                                "stroke-dasharray='" + df.format(failDash) + " " + df.format(circ) + "' " +
                                "stroke-dashoffset='" + df.format(failOffset) + "' " +
                                "transform='rotate(-90 " + cx + " " + cy + ")'/>" : "") +

                // Skipped segment
                (skipped > 0 ?
                        "<circle cx='" + cx + "' cy='" + cy + "' r='" + r + "' " +
                                "fill='none' stroke='#f39c12' stroke-width='28' " +
                                "stroke-dasharray='" + df.format(skipDash) + " " + df.format(circ) + "' " +
                                "stroke-dashoffset='" + df.format(skipOffset) + "' " +
                                "transform='rotate(-90 " + cx + " " + cy + ")'/>" : "") +

                // Centre label
                "<text x='" + cx + "' y='" + (cy - 8) + "' " +
                "text-anchor='middle' font-size='22' font-weight='bold' fill='#2c3e50' " +
                "font-family='Segoe UI,Arial,sans-serif'>" + total + "</text>" +
                "<text x='" + cx + "' y='" + (cy + 12) + "' " +
                "text-anchor='middle' font-size='11' fill='#888' " +
                "font-family='Segoe UI,Arial,sans-serif'>total</text>" +

                "</svg>";
    }
    /**
     * Formats a legend label — e.g. "Passed (80%)"
     * Computed in Java so JS stays logic-free.
     */
    private static String formatLabel(String name, long count, long total) {
        double pct = total > 0 ? (100.0 * count) / total : 0;
        return name + " (" + DECIMAL_FORMAT.format(pct) + "%)";
    }

}