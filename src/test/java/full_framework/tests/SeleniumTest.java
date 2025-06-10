package full_framework.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SeleniumTest {

    WebDriver driver = new ChromeDriver();

    @Test(
            enabled = false
    )
    public void windowHandles() {
        driver.manage().window().maximize();

        driver.get("https://google.com");
        String googleTab = driver.getWindowHandle();
        System.out.println("googleTab = " + googleTab);

        ((JavascriptExecutor) driver).executeScript("window.open()");
        for (String handle: driver.getWindowHandles()) {
            if (!handle.equals(googleTab)) {
                driver.switchTo().window(handle);
                driver.get("https://www.verisk.com/");
                driver.findElement(By.xpath("//*[contains(text(), 'verisk')]"));
            }
        }
    }


    @Test(
            enabled = false
    )
    public void checkBrokenLinks() {
        driver.get("http://www.deadlinkcity.com/");
        List<WebElement> links = driver.findElements(By.tagName("a"));

            for (WebElement link: links) {
                String url = null;
                try {
                    url = link.getAttribute("href");
//                    System.out.println("getAriaRole: " + link.getAriaRole());
                    System.out.println("getAccessibleName: " + link.getAccessibleName());
                    HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
//                    http.setConnectTimeout(3000);
                    http.connect();
                    int response_code = http.getResponseCode();
                    if (response_code >= 400) {
                        System.out.println("link is broken: " + url + response_code);
                    } else {
                        System.out.println("good link: " + url);
                    }
                } catch (Exception e) {
                    System.out.println("e.getMessage() = " + e.getMessage() + url);
                }
            }
    }


    @Test(
            enabled = false
    )
    public void fn() {

    }
}
