// src/main/java/full_framework/utils/ExtentReporter.java
package full_framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ExtentReporter {

    public static ExtentReports extent;
    public static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>(); // For parallel execution
    private static final Logger logger = LogManager.getLogger(ExtentReporter.class);

    private ExtentReporter() {
        // Private constructor to prevent instantiation
    }

    public static void initReports() {
        if (extent == null) {
            logger.info("Initializing ExtentReports...");
            // Create a timestamped folder for reports
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport_" + timestamp + "/";

            // Create the directory if it doesn't exist
            Path path = Paths.get(reportPath);
            try {
                Files.createDirectories(path);
                logger.info("Created report directory: " + reportPath);
            } catch (IOException e) {
                logger.error("Failed to create report directory: " + reportPath, e);
                throw new RuntimeException("Failed to create report directory", e);
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath + "TestReport.html");
            spark.config().setTheme(Theme.STANDARD); // Or Theme.DARK
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("Registration Feature Test Results");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Add system information (optional but good practice)
            extent.setSystemInfo("Host Name", "Localhost");
            extent.setSystemInfo("Environment", ConfigLoader.getStringProperty("environment.name"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Browser", ConfigLoader.getStringProperty("browser"));
            logger.info("ExtentReports initialized successfully.");
        }
    }

    public static void flushReports() {
        if (extent != null) {
            logger.info("Flushing ExtentReports...");
            extent.flush();
            logger.info("ExtentReports flushed successfully.");
        } else {
            logger.warn("ExtentReports instance is null, cannot flush reports.");
        }
    }

    // Method to create a new test in the report
    public static ExtentTest createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test); // Store in ThreadLocal for parallel execution
        logger.info("Created new Extent Test: " + testName);
        return test;
    }

    // Method to get the current test from ThreadLocal
    public static ExtentTest getTest() {
        return extentTest.get();
    }
}