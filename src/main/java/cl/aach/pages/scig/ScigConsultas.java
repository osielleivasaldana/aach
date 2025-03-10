package cl.aach.pages.scig;

import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ScigConsultas {

    // ==============================
    // Constantes
    // ==============================
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // Localizadores
    private static final By CONSULTOR_BUTTON = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[19]/td[2]/div[2]");
    private static final By FECHA_DESDE = By.id("ctl00_ContentPlaceHolder1_FechaDesde");
    private static final By FECHA_HASTA = By.id("ctl00_ContentPlaceHolder1_FechaHasta");
    private static final By BUSCAR_BUTTON = By.id("ctl00_ContentPlaceHolder1_BtnListar");
    private static final By RESULTADO_ESPERADO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_trBotones\"]/td[1]/input");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public ScigConsultas(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * clickConsultor inicia el sistema en el lanzador de aplicaciones.
     */
    public void clickConsultor() {
        tabManager.saveCurrentTab(); // Guarda la pestaña actual antes de abrir una nueva
        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
        boton.click();
        tabManager.switchToNewTab();
    }

    /**
     * buscarRegistros llena el campo "Fecha Desde" mediante JavaScript y hace clic en el botón buscar.
     *
     * @param fecha La fecha a ingresar.
     */
    public void buscarRegistros(String fecha) {
        WebElement fechaElement = driver.findElement(FECHA_DESDE);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].value = arguments[1];", fechaElement, fecha);

        WebElement botonConsultar = wait.until(ExpectedConditions.presenceOfElementLocated(BUSCAR_BUTTON));
        botonConsultar.click();
    }

    /**
     * ValidarResultado valida el resultado de la búsqueda comparando el atributo "value" del elemento.
     *
     * @param expectedText El texto esperado.
     */
    public void ValidarResultado(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO_ESPERADO));
        String actualText = resultLabel.getAttribute("value");
        if (!actualText.equals(expectedText)) {
            throw new AssertionError("Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'");
        } else {
            System.out.println("El texto coincide correctamente: " + actualText);
        }
    }
}
