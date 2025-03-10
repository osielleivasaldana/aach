package cl.aach.pages.sissoap;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
     * clickConsultor inicia el sistema en el lanzador de aplicaciones.
     * Guarda la pestaña actual y cambia a la nueva.
     */
    public void clickConsultor() {
        tabManager.saveCurrentTab();
        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
        boton.click();
        tabManager.switchToNewTab();
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
