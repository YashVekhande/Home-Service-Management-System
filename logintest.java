package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // Set path to ChromeDriver (can also be configured in Jenkins environment)
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        // Open login page
        driver.get("https://example.com/login");
    }

    @Test
    public void testValidLogin() {
        // Locate username field and enter username
        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys("testuser");

        // Locate password field and enter password
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys("testpassword");

        // Click login button
        WebElement loginBtn = driver.findElement(By.id("loginButton"));
        loginBtn.click();

        // Verify successful login by checking dashboard element
        WebElement dashboard = driver.findElement(By.id("dashboard"));
        Assert.assertTrue(dashboard.isDisplayed(), "Login failed!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
