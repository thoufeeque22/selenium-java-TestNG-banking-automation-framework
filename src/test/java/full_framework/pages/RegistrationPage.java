package full_framework.pages;

import com.github.javafaker.Faker;
import full_framework.utils.ConfigLoader;
import full_framework.utils.SeleniumActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class RegistrationPage {
    WebDriver driver;
    private SeleniumActions actions;

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new SeleniumActions(driver);
    }

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

    public String generateUsername() {
        Faker faker = new Faker();
        return faker.name().username();
    }

    public void goToRegPage() {
        driver.get(ConfigLoader.getStringProperty("baseurl") + "register.htm");

    }

    public void registerUser(
            String firstName, String lastName, String street, String city, String state, String zipCode,
            String phone, String ssn, String password
    )
    {

        String username = generateUsername();

        actions.sendKeys(firstNameInput, firstName);
        actions.sendKeys(lastNameInput, lastName);
        actions.sendKeys(streetInput, street);
        actions.sendKeys(cityInput, city);
        actions.sendKeys(stateInput, state);
        actions.sendKeys(zipCodeInput, zipCode);
        actions.sendKeys(phoneNumberInput, phone);
        actions.sendKeys(ssnInput, ssn);
        actions.sendKeys(usernameInput, username);
        actions.sendKeys(passwordInput, password);
        actions.sendKeys(repeatedPwInput, password);
        actions.click(registerBtn);



        Assert.assertTrue(driver.findElement(titleTextEl).getText().contains("Welcome " + username),
                "Verification of welcome message failed!");
        Assert.assertTrue(driver.findElement(AccCreatedTextEl).getText().contains("You are now logged in."),
                "Verification of account creation message failed!");
    }
}
