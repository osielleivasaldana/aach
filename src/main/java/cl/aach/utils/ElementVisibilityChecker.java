package cl.aach.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ElementVisibilityChecker {

    /**
     * Verifica si un elemento es visible en la página.
     *
     * @param driver   Instancia de WebDriver.
     * @param locator  Localizador del elemento (By.id, By.xpath, By.name, etc.).
     * @return         true si el elemento es visible, false si no.
     */
    public static boolean isElementVisible(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element.isDisplayed(); // Devuelve true si el elemento es visible
        } catch (Exception e) {
            // Si el elemento no se encuentra o no es visible, retorna false
            return false;
        }
    }
}
