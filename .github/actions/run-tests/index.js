const core         = require('@actions/core');
const childProcess = require('child_process');
const fs           = require('fs');
const path         = require('path');

// ── Inputs ───────────────────────────────────────────────────────────────────
const _browser     = core.getInput('browser')     || 'chrome';
const _suiteFile   = core.getInput('suiteFile')   || 'testng.xml';


// Report files your TestNG listener generates
const REPORT_DIR   = 'CustomReports';
const HTML_FILES   = ['summary', 'testcaseDetails'];  // matches your filenames exactly

// ─────────────────────────────────────────────────────────────────────────────

async function runTestNG() {
    const testStartTime = dateFormatter();
    core.setOutput('testStartTime', testStartTime);
    const testStart = performance.now();

    // ── Maven command — standard TestNG, no Cucumber profiles needed ──────────
    const mavenCommand =
        `mvn clean test -Dsurefire.suiteXmlFiles=${_suiteFile} -Dbrowser=${_browser}`;

    console.log('Running TestNG suite: ' + mavenCommand);

    try {
        await execute(mavenCommand);
    } catch (error) {
        // Tests may fail (non-zero exit) — we still want reports generated
        // so we log and continue rather than throwing here
        console.log('Maven exited with non-zero code (test failures present): ' + error.message);
    }

    const testFinishTime = dateFormatter();
    core.setOutput('testFinishTime', testFinishTime);
    const testEnd = performance.now();
    core.setOutput('testExecutionTime', calculateDuration(testStart, testEnd));

    // ── Read generated HTML files and expose as step outputs ─────────────────
    readHtmlFilesAsOutputs(HTML_FILES);

    // ── Push to GitHub Actions summary UI ────────────────────────────────────
    await pushToActionsSummary(HTML_FILES);
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * Reads each HTML file from CustomReports/ and sets it as a step output.
 * The workflow email step references these outputs as steps.runTests.outputs.summary etc.
 */
function readHtmlFilesAsOutputs(fileNames) {
    for (const name of fileNames) {
        const filePath = path.join(REPORT_DIR, name + '.html');
        if (fs.existsSync(filePath)) {
            const content = fs.readFileSync(filePath, 'utf8');
            core.setOutput(name, content);
            console.log(`[index.js] Output set for: ${name}`);
        } else {
            console.log(`[index.js] WARNING: Report not found: ${filePath}`);
        }
    }

    // ── Email summary — separate file, SVG chart, no JS ──────────────────────
    const emailSummaryPath = path.join(REPORT_DIR, 'emailSummary.html');
    if (fs.existsSync(emailSummaryPath)) {
        const emailContent = fs.readFileSync(emailSummaryPath, 'utf8');
        core.setOutput('emailSummary', emailContent);
        console.log('[index.js] Email summary output set.');
    }
}

/**
 * Strips <style>, <head>, <title> tags before pushing to GitHub Actions
 * summary UI — same pattern as the reference implementation.
 * Uses regex instead of JSDOM to avoid the npm dependency.
 */
async function pushToActionsSummary(fileNames) {
    try {
        core.summary.addHeading('TestNG Execution Results', 3);

        for (const name of fileNames) {
            const filePath = path.join(REPORT_DIR, name + '.html');
            if (!fs.existsSync(filePath)) continue;

            let html = fs.readFileSync(filePath, 'utf8');

            // Strip tags that break GitHub summary rendering
            html = html
                .replace(/<style[^>]*>[\s\S]*?<\/style>/gi, '')
                .replace(/<head[^>]*>[\s\S]*?<\/head>/gi,  '')
                .replace(/<title[^>]*>[\s\S]*?<\/title>/gi,'')
                .replace(/<script[^>]*>[\s\S]*?<\/script>/gi, '') // strip canvas JS
                .replace(/\s+/g, ' ')
                .trim();

            if (name === 'summary') {
                core.summary.addRaw(html, true);
            } else {
                core.summary.addDetails('Test Case Details', html);
            }
        }
        await core.summary.write();
    } catch (error) {
        console.log('[index.js] ERROR writing Actions summary: ' + error.message);
    }
}

// ── Utilities ─────────────────────────────────────────────────────────────────

function execute(command) {
    return new Promise((resolve, reject) => {
        childProcess
            .exec(command, { maxBuffer: undefined }, (error, stdout, stderr) => {
                if (error) { reject(error); return; }
                resolve(stdout);
            })
            .stdout.pipe(process.stdout);
    });
}

function dateFormatter() {
    return new Date().toISOString().replace('Z', '+0000');
}

function calculateDuration(start, end) {
    let seconds = Math.floor((end - start) / 1000);
    if (seconds >= 3600) {
        const h = Math.floor(seconds / 3600);
        const m = Math.floor((seconds % 3600) / 60);
        return `${String(h).padStart(2,'0')}h${String(m).padStart(2,'0')}m`;
    } else if (seconds >= 60) {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        return `${String(m).padStart(2,'0')}m${String(s).padStart(2,'0')}s`;
    }
    return `${String(seconds).padStart(2,'0')}s`;
}

// ADD this at the very bottom — replaces module.exports
runTestNG().catch(error => {
    core.setFailed(error.message);
});