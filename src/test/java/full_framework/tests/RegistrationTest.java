package full_framework.tests;

import full_framework.base.BaseTest;
import full_framework.pages.RegistrationPage;
import full_framework.utils.ConfigLoader;
import full_framework.utils.ExcelReader;
import org.testng.annotations.*;

public class RegistrationTest extends BaseTest {

    @DataProvider(name = "RegistrationDataFromExcel")
    public Object[][] getRegistrationDataFromExcel() {
        String excelFilePath = System.getProperty("user.dir") + ConfigLoader.getStringProperty("testdata.excel.path");
        String sheetName = System.getProperty("user.dir") + ConfigLoader.getStringProperty("registration.excel.sheetname");
        ExcelReader reader = new ExcelReader(excelFilePath, sheetName);
        return reader.getTableData();
    }


    @Test(
            dataProvider = "RegistrationDataFromExcel"
    )
    public void testSuccessfulRegistration(
            String firstName, String lastName, String street, String city, String state, String zipCode,
            String phone, String ssn, String password
    ) {
        RegistrationPage registrationPage = new RegistrationPage(driver);
        registrationPage.goToRegPage();
        registrationPage.registerUser(firstName, lastName, street, city, state, zipCode, phone, ssn, password);

    }

}
