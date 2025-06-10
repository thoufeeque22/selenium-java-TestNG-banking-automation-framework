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
                    chromeOptions.addArguments("--headless=new"); // For new headless mode (or --headless for older versions)
                    chromeOptions.addArguments("--disable-gpu"); // Recommended for headless
                }

                // --- NEW FIX FOR 'USER DATA DIRECTORY IN USE' ERROR ---
                // These arguments are crucial for running Chrome in CI/CD (especially Linux/Docker)
                chromeOptions.addArguments("--no-sandbox"); // Required when running as root (common in containers)
                chromeOptions.addArguments("--disable-dev-shm-usage"); // Overcome limited /dev/shm space in containers
                chromeOptions.addArguments("--window-size=1920,1080"); // Standardize resolution for consistent screenshots
                chromeOptions.addArguments("--incognito"); // Crucial: Prevents using or creating a persistent user profile
                // You can also try "--guest" instead of "--incognito" if needed, but incognito is more common.

                // Removed the try-catch block for Files.createTempDirectory as it's no longer needed.
                // That approach was sometimes problematic in certain CI environments.

                // --- END NEW FIX ---

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