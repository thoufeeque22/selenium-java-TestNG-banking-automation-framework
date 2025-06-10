// src/full_framework/base/BaseTest.java
package full_framework.base;

import full_framework.utils.ConfigLoader;
import io.github.bonigarcia.wdm.WebDriverManager; // Import WebDriverManager
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseTest {

    public WebDriver driver;
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
        int explicitWaitSeconds = Integer.parseInt(ConfigLoader.getStringProperty("explicit.wait.seconds"));

        logger.info("Initializing WebDriver for browser: " + browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                // Use WebDriverManager to setup ChromeDriver
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                logger.info("ChromeDriver initialized via WebDriverManager.");
                break;
            case "firefox":
                // Use WebDriverManager to setup FirefoxDriver
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                logger.info("FirefoxDriver initialized via WebDriverManager.");
                break;
            case "edge":
                // Use WebDriverManager to setup EdgeDriver
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                logger.info("EdgeDriver initialized via WebDriverManager.");
                break;
            // Add more browsers as needed
            default:
                logger.error("Browser '" + browser + "' is not supported. Please check config.properties.");
                throw new IllegalArgumentException("Browser '" + browser + "' is not supported.");
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSeconds));
        logger.info("WebDriver initialized. Maximize window, implicit wait: " + implicitWaitSeconds + "s, explicit wait default: " + explicitWaitSeconds + "s.");
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