package cl.aach.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DocumentManager {

    private final WebDriver driver;

    // Localizadores
    private final By tabla = By.xpath("//*[@id='body_GridViewDocuments']");
    private final By opcionesDoc = By.xpath(".//td[@data-title='Opciones']/div");
    private final By alertAccept = By.id("alertAccept");

    public DocumentManager(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Elimina todos los documentos de la tabla que contengan 'archivoParcial' en la columna 'Archivo'.
     * Repite el proceso mientras siga encontrando coincidencias.
     *
     * @param archivoParcial Texto parcial a buscar en la columna "Archivo".
     */
    public void eliminarDocumentosTabla(String archivoParcial) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean found;

        do {
            found = false;

            // 1. Esperar a que la tabla aparezca en el DOM.
            wait.until(ExpectedConditions.presenceOfElementLocated(tabla));

            // 2. Localizar la tabla y sus filas.
            WebElement tableElement = driver.findElement(tabla);
            List<WebElement> filas = tableElement.findElements(By.tagName("tr"));

            for (WebElement fila : filas) {
                // Intentar obtener la columna "Archivo" en esta fila.
                WebElement columnaArchivo;
                try {
                    columnaArchivo = fila.findElement(By.xpath(".//td[@data-title='Archivo']"));
                } catch (Exception e) {
                    // Si no existe la columna "Archivo" en esta fila, continuar con la siguiente.
                    continue;
                }

                String textoArchivo = columnaArchivo.getText();
                // Verificar si coincide con el texto buscado.
                if (textoArchivo.contains(archivoParcial)) {
                    found = true; // Se encontró un documento a eliminar.

                    // 3. Clic en el botón "Opciones".
                    WebElement botonOpciones = fila.findElement(opcionesDoc);
                    botonOpciones.click();

                    // 4. Esperar a que aparezca y sea clickable el enlace "Borrar" en esta fila.
                    By borrarLink = By.xpath(".//div[contains(@id, 'menuOptionsDocuments')]/a[contains(text(), 'Borrar')]");
                    WebElement botonBorrar = wait.until(ExpectedConditions.elementToBeClickable(fila.findElement(borrarLink)));
                    botonBorrar.click();

                    // 5. Esperar y hacer clic en el botón "alertAccept" si está presente.
                    wait.until(ExpectedConditions.visibilityOfElementLocated(alertAccept));
                    WebElement alertButton = driver.findElement(alertAccept);
                    alertButton.click();

                    // 6. Esperar a que la tabla se refresque antes de seguir buscando.
                    wait.until(ExpectedConditions.stalenessOf(tableElement));
                    break; // Romper el for para reiniciar el ciclo do-while si se encontró algo.
                }
            }
        } while (found); // Se repite mientras se eliminen coincidencias.
    }
}
