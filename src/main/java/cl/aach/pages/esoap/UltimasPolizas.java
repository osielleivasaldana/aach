package cl.aach.pages.esoap;

import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UltimasPolizas {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//div[text()='ADMINISTRADOR ESOAP']");
    private static final By BUSCAR_BUTTON = By.id("masterContenido_lnkBusqCia");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"masterContenido_GridResultado\"]/tbody/tr[1]/th[1]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public UltimasPolizas(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
     * Hace clic en el botón de búsqueda para obtener las últimas pólizas.
     */
    public void buscarPoliza() {
        driver.findElement(BUSCAR_BUTTON).click();
    }

    /**
     * Valida el resultado de la búsqueda comparando el texto obtenido con el esperado.
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
