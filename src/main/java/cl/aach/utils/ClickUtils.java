package cl.aach.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Clase utilitaria para manejar clics en elementos ocultos o no visibles.
 */
public class ClickUtils {

    /**
     * Método para hacer clic en un elemento no visible utilizando JavaScript.
     *
     * @param driver  Instancia del WebDriver.
     * @param element El WebElement en el que se desea hacer clic.
     */
    public static void click(WebDriver driver, WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].click();", element);
    }
}
