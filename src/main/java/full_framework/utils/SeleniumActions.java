// src/main/java/full_framework/utils/SeleniumActions.java
package full_framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumActions {

    private WebDriver driver;
    private WebDriverWait wait;
    private final long EXPLICIT_WAIT_SECONDS; // Use final for constants
    private static final Logger logger = LogManager.getLogger(SeleniumActions.class); // Logger for SeleniumActions

    public SeleniumActions(WebDriver driver) {
        this.driver = driver;
        this.EXPLICIT_WAIT_SECONDS = Long.parseLong(ConfigLoader.getStringProperty("explicit.wait.seconds"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
        logger.debug("SeleniumActions initialized with explicit wait: " + EXPLICIT_WAIT_SECONDS + " seconds.");
    }

    public WebElement waitForElementToBeClickable(By locator) {
        logger.debug("Waiting for element to be clickable: " + locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForElementToBeVisible(By locator) {
        logger.debug("Waiting for element to be visible: " + locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForElementToBePresent(By locator) {
        logger.debug("Waiting for element to be present: " + locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void click(By locator) {
        logger.info("Clicking element: " + locator);
        waitForElementToBeClickable(locator).click();
    }

    public void sendKeys(By locator, String text) {
        logger.info("Entering text '" + text + "' into element: " + locator);
        waitForElementToBeVisible(locator).sendKeys(text);
    }

    public String getText(By locator) {
        String text = waitForElementToBeVisible(locator).getText();
        logger.info("Retrieved text '" + text + "' from element: " + locator);
        return text;
    }

    public boolean isElementDisplayed(By locator) {
        logger.debug("Checking if element is displayed: " + locator);
        try {
            boolean displayed = waitForElementToBeVisible(locator).isDisplayed();
            logger.debug("Element " + locator + " is displayed: " + displayed);
            return displayed;
        } catch (Exception e) {
            logger.warn("Element " + locator + " not displayed within explicit wait time.", e);
            return false;
        }
    }
}