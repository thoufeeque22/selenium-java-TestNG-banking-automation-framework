// src/full_framework/base/BaseTest.java
package full_framework.base;

import full_framework.utils.ConfigLoader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver; // Import EdgeDriver

import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        boolean headless = Boolean.parseBoolean(ConfigLoader.getStringProperty("headless_mode"));
        int implicitWaitSeconds = Integer.parseInt(ConfigLoader.getStringProperty("implicit.wait.seconds"));
        int explicitWaitSeconds = Integer.parseInt(ConfigLoader.getStringProperty("explicit.wait.seconds"));

        logger.info("Initializing WebDriver for browser: " + browser + (headless ? " (headless)" : ""));

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();

                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--disable-gpu");
                }

                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--window-size=1920,1080");

                try {
                    File tempUserDataDir = Files.createTempDirectory("chrome_profile_").toFile();
                    chromeOptions.addArguments("--user-data-dir=" + tempUserDataDir.getAbsolutePath());
                    logger.info("Using temporary user data directory for Chrome: " + tempUserDataDir.getAbsolutePath());

                } catch (IOException e) {
                    logger.error("Failed to create temporary user data directory for Chrome: " + e.getMessage(), e);
                    // Re-throw the exception to fail the test setup if this is critical
                    throw new RuntimeException("Could not set up temporary user data directory for Chrome", e);
                }
                // --- ADDED THIS LINE ---
                driver = new ChromeDriver(chromeOptions);
                logger.info("ChromeDriver initialized via WebDriverManager.");
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                logger.info("FirefoxDriver initialized via WebDriverManager.");
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                // --- CORRECTED THIS LINE ---
                driver = new EdgeDriver(); // Use EdgeDriver for Edge browser
                logger.info("EdgeDriver initialized via WebDriverManager.");
                break;
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