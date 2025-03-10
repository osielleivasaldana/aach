package cl.aach.pages.esoap;

import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EsoapConsultaPorPoliza {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//div[text()='ADMINISTRADOR ESOAP']");
    private static final By POLIZA_INPUT = By.xpath("//*[@id=\"masterContenido_txtNroPoliza\"]");
    private static final By BUSCAR_BUTTON = By.id("masterContenido_btnLnkBuscar");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"masterContenido_GridResultado\"]/tbody/tr[2]/td[9]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public EsoapConsultaPorPoliza(WebDriver driver) {
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
     * Ingresa una póliza en el campo correspondiente y hace clic en el botón buscar.
     *
     * @param poliza La póliza a buscar.
     */
    public void enterPoliza(String poliza) {
        driver.findElement(POLIZA_INPUT).sendKeys(poliza);
        driver.findElement(BUSCAR_BUTTON).click();
    }

    /**
     * Valida el resultado de la búsqueda comparando el texto obtenido con el esperado.
     *
     * @param expectedText El texto esperado en el resultado.
     */
    public void validateResult(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO_LABEL));
        String actualText = resultLabel.getText();
        assert actualText.equals(expectedText) :
                "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }
}
