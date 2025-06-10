// src/full_framework/base/BaseTest.java
package full_framework.base;

import full_framework.utils.ConfigLoader;
import org.apache.logging.log4j.LogManager; // Import LogManager
import org.apache.logging.log4j.Logger;   // Import Logger
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    // Initialize a logger for the BaseTest class
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeSuite
    public void globalSetup() {
        logger.info("--- Global Setup Started (BeforeSuite) ---");
        ConfigLoader.loadProperties();
        logger.info("Configuration properties loaded successfully.");
    }

    @BeforeMethod
    public void setup() {
        String browser = ConfigLoader.getStringProperty("browser");
        int implicitWaitSeconds = Integer.parseInt(ConfigLoader.getStringProperty("implicit.wait.seconds"));
        int explicitWaitSeconds = Integer.parseInt(ConfigLoader.getStringProperty("explicit.wait.seconds")); // Log explicit wait too

        logger.info("Initializing WebDriver for browser: {}", browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            default:
                logger.error("Browser '{}' is not supported. Please check config.properties.", browser);
                throw new IllegalArgumentException("Browser '" + browser + "' is not supported.");
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSeconds));
        logger.info("WebDriver initialized. Maximize window, implicit wait: {}s, explicit wait default: {}s.", implicitWaitSeconds, explicitWaitSeconds);
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            logger.info("Quitting WebDriver.");
            driver.quit();
        } else {
            logger.warn("WebDriver instance was null during teardown.");
        }
        logger.info("--- Test Teardown Completed (AfterMethod) ---");
    }

    @AfterSuite
    public void globalTeardown() {
        logger.info("--- Global Teardown Completed (AfterSuite) ---");
    }
}