package cl.aach.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class EliminarNormativa {

    private final WebDriver driver;

    // Localizadores
    private final By tablaNormativas = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_DivResultados\"]/table");
    private final By columnaNombre = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_DivResultados\"]/table/tbody/tr[1]/th[3]");
    private final By botonModificar = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_DivResultados\"]/table/tbody/tr[2]/td[2]/a");
    private final By botonEliminar = By.xpath(".//a[contains(@href, 'Eliminar')]");
    private final By confirmacionEliminar = By.id("alertAccept");

    public EliminarNormativa(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Modificar una normativa en la tabla.
     */
    public void modificarNormativa() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(tablaNormativas));

        WebElement tableElement = driver.findElement(tablaNormativas);
        List<WebElement> filas = tableElement.findElements(By.tagName("tr"));

        for (WebElement fila : filas) {
            try {
                // Buscar la columna "NOMBRE"
                WebElement columnaNombreElemento = fila.findElement(columnaNombre);
                String textoNombre = columnaNombreElemento.getText();

                // Verificar si contiene "Normativa"
                if (textoNombre.equalsIgnoreCase("NORMATIVA")) {
                    // Hacer clic en el botón "Modificar"
                    WebElement botonModificarElemento = fila.findElement(botonModificar);
                    botonModificarElemento.click();

                    System.out.println("Modificación iniciada para 'Normativa'.");
                    break; // Salir después de la primera coincidencia
                }
            } catch (Exception e) {
                // Continuar si algún elemento no está presente
                continue;
            }
        }
    }

    /**
     * Eliminar una normativa en la tabla.
     */
    public void eliminarNormativa() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(tablaNormativas));

        WebElement tableElement = driver.findElement(tablaNormativas);
        List<WebElement> filas = tableElement.findElements(By.tagName("tr"));

        for (WebElement fila : filas) {
            try {
                // Buscar la columna "NOMBRE"
                WebElement columnaNombreElemento = fila.findElement(columnaNombre);
                String textoNombre = columnaNombreElemento.getText();

                // Verificar si contiene "Normativa"
                if (textoNombre.equalsIgnoreCase("NORMATIVA")) {
                    // Hacer clic en el botón "Eliminar"
                    WebElement botonEliminarElemento = fila.findElement(botonModificar);
                    botonEliminarElemento.click();

                    // Confirmar eliminación
                    wait.until(ExpectedConditions.visibilityOfElementLocated(confirmacionEliminar));
                    WebElement confirmacionElemento = driver.findElement(confirmacionEliminar);
                    confirmacionElemento.click();

                    System.out.println("Normativa eliminada con éxito.");
                    break; // Salir después de la primera coincidencia
                }
            } catch (Exception e) {
                // Continuar si algún elemento no está presente
                System.out.println("No hay elementos presentes");
                continue;
            }
        }
    }
}
