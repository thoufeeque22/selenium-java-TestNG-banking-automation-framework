// src/full_framework/pages/RegistrationPage.java
package full_framework.pages;

import com.github.javafaker.Faker;
import full_framework.utils.ConfigLoader;
import full_framework.utils.SeleniumActions;
import org.apache.logging.log4j.LogManager; // Import LogManager
import org.apache.logging.log4j.Logger;   // Import Logger
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class RegistrationPage {
    private WebDriver driver;
    private SeleniumActions actions;
    // Initialize a logger for the RegistrationPage class
    private static final Logger logger = LogManager.getLogger(RegistrationPage.class);

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new SeleniumActions(driver);
        logger.debug("RegistrationPage initialized with WebDriver."); // Use debug level for constructor
    }

    // ... (Your By locators remain the same) ...
    By firstNameInput = By.cssSelector("[id='customer.firstName']");
    By lastNameInput = By.cssSelector("[id='customer.lastName']");
    By streetInput = By.cssSelector("[id='customer.address.street']");
    By cityInput = By.cssSelector("[id='customer.address.city']");
    By stateInput = By.cssSelector("[id='customer.address.state']");
    By zipCodeInput = By.cssSelector("[id='customer.address.zipCode']");
    By phoneNumberInput = By.cssSelector("[id='customer.phoneNumber']");
    By ssnInput = By.cssSelector("[id='customer.ssn']");
    By usernameInput = By.cssSelector("[id='customer.username']");
    By passwordInput = By.cssSelector("[id='customer.password']");
    By repeatedPwInput = By.cssSelector("#repeatedPassword");
    By registerBtn = By.cssSelector("[value='Register']");
    By titleTextEl = By.cssSelector(".title");
    By AccCreatedTextEl = By.cssSelector("#rightPanel p");
    By errorElement = By.cssSelector(".error");


    public String generateUsername() {
        logger.info("Generating unique username.");
        Faker faker = new Faker();
        return faker.name().username();
    }

    public void goToRegPage() {
        String url = ConfigLoader.getStringProperty("baseurl") + "register.htm";
        driver.get(url);
        logger.info("Navigated to Registration Page: " + url);
    }

    public void registerUser(String firstName, String lastName, String street, String city,
                             String state, String zipCode, String phone, String ssn, String password) {

        String username = generateUsername();
        logger.info("Attempting to register user with username: " + username + " and First Name: " + firstName);

        actions.sendKeys(firstNameInput, firstName); logger.debug("Entered First Name: {}", firstName);
        actions.sendKeys(lastNameInput, lastName); logger.debug("Entered Last Name: " + lastName);
        actions.sendKeys(streetInput, street); logger.debug("Entered Street: " + street);
        actions.sendKeys(cityInput, city); logger.debug("Entered City: " + city);
        actions.sendKeys(stateInput, state); logger.debug("Entered State: " + state);
        actions.sendKeys(zipCodeInput, zipCode); logger.debug("Entered Zip Code: " + zipCode);
        actions.sendKeys(phoneNumberInput, phone); logger.debug("Entered Phone Number: " + phone);
        actions.sendKeys(ssnInput, ssn); logger.debug("Entered SSN."); // Avoid logging sensitive data
        actions.sendKeys(usernameInput, username); logger.debug("Entered Username: " + username);
        actions.sendKeys(passwordInput, password); logger.debug("Entered Password."); // Avoid logging sensitive data
        actions.sendKeys(repeatedPwInput, password); logger.debug("Entered Repeated Password.");
        actions.click(registerBtn); logger.info("Clicked Register button.");

        // Assertions for success
        String welcomeText = actions.getText(titleTextEl);
        Assert.assertTrue(welcomeText.contains("Welcome " + username),
                "Verification of welcome message failed! Expected 'Welcome " + username + "' but found: " + welcomeText);
        logger.info("Welcome message verified: " + welcomeText);

        String accountCreationText = actions.getText(AccCreatedTextEl);
        Assert.assertTrue(accountCreationText.contains("You are now logged in."),
                "Verification of account creation message failed! Expected 'You are now logged in.' but found: " + accountCreationText);
        logger.info("Account creation message verified: " + accountCreationText);
    }

    public void verifyRegistrationFailure(String expectedErrorMessage) {
        logger.warn("Verifying registration failure with expected error message: " + expectedErrorMessage);
        Assert.assertTrue(actions.isElementDisplayed(errorElement),
                "Error message element is not displayed!");
        Assert.assertTrue(actions.getText(errorElement).contains(expectedErrorMessage),
                "Actual error message does not contain: " + expectedErrorMessage);
        logger.info("Registration failure verified. Actual error: " + actions.getText(errorElement));
    }
}