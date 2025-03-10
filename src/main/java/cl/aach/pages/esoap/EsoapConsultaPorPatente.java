package cl.aach.pages.esoap;

import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EsoapConsultaPorPatente {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//div[text()='ADMINISTRADOR ESOAP']");
    private static final By PATENTE_INPUT_A = By.xpath("//*[@id=\"masterContenido_txtPatente1\"]");
    private static final By PATENTE_INPUT_B = By.xpath("//*[@id=\"masterContenido_txtPatente2\"]");
    private static final By BUSCAR_BUTTON = By.id("masterContenido_lnkBusqPatente");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"masterContenido_GridResultado\"]/tbody/tr[2]/td[6]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public EsoapConsultaPorPatente(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * clickConsultor es el encargado de iniciar el sistema en el lanzador de aplicaciones.
     */
    public void clickConsultor() {
        tabManager.saveCurrentTab(); // Guarda la pestaña actual antes de abrir una nueva
        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
        boton.click();
        tabManager.switchToNewTab();
    }

    /**
     * Ingresamos la patente en los campos y hacemos clic en el botón buscar.
     *
     * @param patenteA Primer parte de la patente.
     * @param patenteB Segunda parte de la patente.
     */
    public void enterPatente(String patenteA, String patenteB) {
        driver.findElement(PATENTE_INPUT_A).sendKeys(patenteA);
        driver.findElement(PATENTE_INPUT_B).sendKeys(patenteB);
        driver.findElement(BUSCAR_BUTTON).click();
    }

    /**
     * Valida el resultado de la búsqueda comparándolo con el texto esperado.
     *
     * @param expectedText Texto esperado en el resultado.
     */
    public void validateResult(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO_LABEL));
        String actualText = resultLabel.getText();
        assert actualText.equals(expectedText) :
                "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }
}
