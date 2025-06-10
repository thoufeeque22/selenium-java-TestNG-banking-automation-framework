// src/test/java/full_framework/listeners/TestNGListener.java
package full_framework.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import full_framework.base.BaseTest; // Needed to access the driver for screenshots
import full_framework.utils.ExtentReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestNGListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestNGListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite '" + context.getName() + "' started.");
        ExtentReporter.initReports(); // Initialize reports at the start of the suite
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite '" + context.getName() + "' finished. Total tests: " + context.getAllTestMethods().length);
        ExtentReporter.flushReports(); // Flush reports at the end of the suite
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: " + result.getMethod().getMethodName());
        // Create a new test entry in the Extent Report
        ExtentReporter.createTest(result.getMethod().getMethodName());
        ExtentReporter.getTest().assignCategory(result.getMethod().getRealClass().getSimpleName());
        ExtentReporter.getTest().info("Test started for method: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test PASSED: " + result.getMethod().getMethodName());
        ExtentReporter.getTest().log(Status.PASS, "Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test FAILED: " + result.getMethod().getMethodName(), result.getThrowable());
        ExtentReporter.getTest().log(Status.FAIL, "Test Failed: " + result.getMethod().getMethodName());
        ExtentReporter.getTest().fail(result.getThrowable()); // Log the exception

        // Take screenshot on failure
        WebDriver driver = ((BaseTest) result.getInstance()).driver; // Get WebDriver from the test instance
        if (driver instanceof TakesScreenshot) {
            String screenshotName = result.getMethod().getMethodName() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String screenshotPath = takeScreenshot(driver, screenshotName);
            if (screenshotPath != null) {
                ExtentReporter.getTest().fail("Screenshot on Failure:", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                logger.info("Screenshot captured for failed test: " + screenshotPath);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test SKIPPED: " + result.getMethod().getMethodName());
        ExtentReporter.getTest().log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not commonly used for UI automation
    }

    private String takeScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            logger.warn("WebDriver instance is null, cannot take screenshot.");
            return null;
        }
        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = screenshotName + ".png";
            String directory = System.getProperty("user.dir") + "/reports/ExtentReport_" + timestamp.substring(0, 8) + timestamp.substring(9,15) + "/screenshots/"; // Matches report folder
            Path path = Paths.get(directory);
            Files.createDirectories(path); // Ensure directory exists

            Path fullPath = Paths.get(directory, fileName);
            Files.write(fullPath, screenshotBytes);
            logger.debug("Screenshot saved to: " + fullPath.toString());
            // Return relative path for ExtentReports
            return "../screenshots/" + fileName; // Adjust path relative to the HTML report file
        } catch (IOException e) {
            logger.error("Failed to take screenshot: " + e.getMessage(), e);
            return null;
        }
    }
}