package cl.aach.pages.comite;

import cl.aach.utils.DocumentManager;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;

public class ComiteEliminarDocumentos {

    // ==============================
    // Localizadores (constantes)
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//div[contains(@name, 'GJE') and normalize-space()='COORDINADOR COMITES']");
    private static final By DOCUMENTOS_BUTTON = By.id("DOCUMENTS206");
    private static final By PROPIEDADES_DOCUMENTO = By.xpath("//*[@id=\"body_GridViewDocuments\"]/tbody/tr[1]/td[1]/div[1]");
    private static final By TABLA = By.xpath("//*[@id=\"body_GridViewDocuments\"]");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public ComiteEliminarDocumentos(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos públicos
    // ==============================

    /**
     * clickConsultor es el encargado de iniciar el sistema en el lanzador de aplicaciones.
     * Maneja la apertura de una nueva pestaña y las posibles alertas.
     */
    public void clickConsultor() {
        tabManager.saveCurrentTab(); // Guarda la pestaña actual antes de abrir una nueva
        System.out.println("Pestaña guardada antes de hacer clic en consultor");

        // Esperar a que el botón esté presente y sea visible
        WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(CONSULTOR_BUTTON));
        System.out.println("Botón 'consultor' localizado.");

        // Verificar el número inicial de pestañas
        int initialTabCount = driver.getWindowHandles().size();
        System.out.println("Número de pestañas antes del clic: " + initialTabCount);

        // Hacer clic en el botón
        try {
            boton.click();
            System.out.println("Se hizo clic en el botón 'consultor'.");
        } catch (Exception e) {
            // Si falla el clic normal, intentar con JavaScript
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", boton);
                System.out.println("Se hizo clic en el botón 'consultor' usando JavaScript.");
            } catch (Exception e2) {
                System.err.println("Error al hacer clic en el botón 'consultor': " + e2.getMessage());
                return;
            }
        }

        // Verificar si aparece una alerta
        try {
            // Esperar brevemente por una alerta
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            alertWait.until(ExpectedConditions.alertIsPresent());

            // Si hay una alerta, capturar su mensaje y aceptarla
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Alerta detectada: " + alertText);
            alert.accept();
            System.out.println("Alerta aceptada.");

            // No continuar con el cambio de pestaña
            return;
        } catch (Exception e) {
            // No hay alerta, continuamos normalmente
            System.out.println("No se detectó ninguna alerta.");
        }

        // Esperar un momento para que se abra la nueva pestaña
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verificar si se abrió una nueva pestaña
        int finalTabCount = driver.getWindowHandles().size();
        System.out.println("Número de pestañas después del clic: " + finalTabCount);

        if (finalTabCount > initialTabCount) {
            // Se abrió una nueva pestaña, cambiar a ella
            tabManager.switchToNewTab();
            System.out.println("Cambio a la nueva pestaña realizado.");

            // Esperar a que la nueva página cargue
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            System.err.println("ADVERTENCIA: No se abrió una nueva pestaña después del clic.");
        }
    }

    /**
     * flujoAcciones hace clic en los enlaces y botones necesarios para llegar al formulario para eliminar archivos.
     */
    public void flujoAcciones() {
        wait.until(ExpectedConditions.elementToBeClickable(DOCUMENTOS_BUTTON)).click();
    }

    /**
     * eliminarDocumento elimina todos los documentos relacionados con los test.
     */
    public void eliminarDocumento(String doc1) {
        DocumentManager documentManager = new DocumentManager(driver);
        documentManager.eliminarDocumentosTabla("documentoPruebaAutomatizada.pdf");
    }
}
