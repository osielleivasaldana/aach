package cl.aach.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigUtil {

    private static final Logger LOGGER = Logger.getLogger(ConfigUtil.class.getName());
    private static Properties properties;

    static {
        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties")) {
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            // Log the error with a descriptive message
            LOGGER.log(Level.SEVERE, "Error al cargar el archivo config.properties", e);
            throw new RuntimeException("No se pudo cargar el archivo config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void clickHiddenElement(WebDriver driver, WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].click();", element);
    }
}
