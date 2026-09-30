package com.qa.aem.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.qa.aem.utils.ConfigReader;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

/**
 * Writes the test results to an Extent HTML report at target/extent-report/index.html.
 */
public class ExtentReportListener implements ITestListener {

    private static final ExtentReports extent = createReport();
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    private static ExtentReports createReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report/index.html");
        spark.config().setDocumentTitle("AEM DAM API Automation");
        spark.config().setReportName("AEM DAM API Tests");

        ExtentReports report = new ExtentReports();
        report.attachReporter(spark);
        report.setSystemInfo("Environment", ConfigReader.getEnvironment());
        return report;
    }

    @Override
    public void onTestStart(ITestResult result) {
        startTest(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        currentTest.get().pass("Test passed");
        currentTest.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        currentTest.get().fail(result.getThrowable());
        currentTest.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (currentTest.get() == null) {
            startTest(result);
        }
        currentTest.get().skip(result.getThrowable() == null ? "Test skipped" : result.getThrowable().getMessage());
        currentTest.remove();
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private static synchronized void startTest(ITestResult result) {
        String name = result.getMethod().getMethodName();
        if (result.getParameters().length > 0) {
            name = name + " " + Arrays.toString(result.getParameters());
        }
        currentTest.set(extent.createTest(name, result.getMethod().getDescription()));
    }
}
