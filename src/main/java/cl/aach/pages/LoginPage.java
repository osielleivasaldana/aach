package cl.aach.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    private By emailField = By.id("ctl00_body_txUsuario");
    private By passwordField = By.id("ctl00_body_txPassword");
    private By loginButton = By.xpath("//*[@id=\"aspnetForm\"]/div[4]/div/div/main/section/div[2]/div[1]/div/div[3]/div/button");

    public void login(String email, String password) {
        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passwordField).sendKeys(password);

       driver.findElement(loginButton).click();
    }
}