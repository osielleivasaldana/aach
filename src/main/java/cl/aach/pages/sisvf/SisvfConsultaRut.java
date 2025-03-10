package cl.aach.pages.sisvf;

import cl.aach.utils.ClickUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class SisvfConsultaRut {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[11]/td[2]/div[2]");
    private static final By MODAL_BUTTON = By.xpath("//*[@id=\"tdMensajes\"]/table/tbody/tr[2]/td/input");
    private static final By BUSCAR_EN_LINEA_BUTTON = By.id("masterContenido_btnBusquedaLinea");
    private static final By RUT_INPUT = By.xpath("//*[@id=\"txtbusRUT\"]");
    private static final By BUSCAR_BUTTON = By.xpath("//*[@id=\"BtnConsultar\"]");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"GridConsultaLinea\"]/tbody/tr[2]/td[5]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private String originalTab;

    // ==============================
    // Constructor
    // ==============================
    public SisvfConsultaRut(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * Hace clic en el botón de Consultor del módulo SISGEN.
     * Guarda la pestaña actual, hace clic en el modal y cambia a la nueva pestaña.
     */
    public void clickConsultor() {
        saveCurrentTab();
        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
        ClickUtils.click(driver, boton);

        WebElement modal = wait.until(ExpectedConditions.presenceOfElementLocated(MODAL_BUTTON));
        ClickUtils.click(driver, modal);

        switchToNewTab();
    }

    /**
     * Hace clic en el botón "Buscar en Línea".
     */
    public void clickBuscarEnLinea() {
        wait.until(ExpectedConditions.elementToBeClickable(BUSCAR_EN_LINEA_BUTTON)).click();
    }

    /**
     * Ingresa el RUT en el campo correspondiente y hace clic en el botón buscar.
     *
     * @param rut El RUT a buscar.
     */
    public void enterRut(String rut) {
        driver.findElement(RUT_INPUT).sendKeys(rut);
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

    // ==============================
    // Métodos Auxiliares
    // ==============================

    /**
     * Guarda el identificador de la pestaña actual.
     */
    private void saveCurrentTab() {
        originalTab = driver.getWindowHandle();
    }

    /**
     * Cambia el foco a la nueva pestaña.
     */
    private void switchToNewTab() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfWindowsToBe(2));
        Set<String> allTabs = driver.getWindowHandles();
        for (String tab : allTabs) {
            if (!tab.equals(originalTab)) {
                driver.switchTo().window(tab);
                break;
            }
        }
    }
}
