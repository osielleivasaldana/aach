package cl.aach.pages.sissoap;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;

public class SissoapConsultaPorPatente {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[2]/td[2]/div[2]");
    private static final By PATENTE_INPUT = By.xpath("//*[@id=\"masterContenido_txtPatenteABuscar\"]");
    private static final By BUSCAR_BUTTON = By.id("masterContenido_cmdBuscaPorPatente");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"masterContenido_grdDatos\"]/tbody/tr[2]/td[2]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public SissoapConsultaPorPatente(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
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
     * Ingresa la patente en el campo correspondiente y hace clic en el botón buscar.
     *
     * @param patente La patente a buscar.
     */
    public void enterPatente(String patente) {
        driver.findElement(PATENTE_INPUT).sendKeys(patente);
        driver.findElement(BUSCAR_BUTTON).click();
    }

    /**
     * Valida el resultado de la búsqueda comparando el texto obtenido con el texto esperado.
     *
     * @param expectedText El texto esperado.
     */
    public void validateResult(String expectedText) {
        WebElement result = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO_LABEL));
        String actualText = result.getText();
        assert actualText.equals(expectedText)
                : "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }
}
