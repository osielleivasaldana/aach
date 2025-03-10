package cl.aach.pages.sisvf;

import cl.aach.utils.ClickUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class SisvfConsultaNombres {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[11]/td[2]/div[2]");
    private static final By MODAL_BUTTON = By.xpath("//*[@id=\"tdMensajes\"]/table/tbody/tr[2]/td/input");
    private static final By TIPO_DE_BUSQUEDA = By.id("Rbt_TipoDeBusqueda_2");
    private static final By AP_PATERNO_INPUT = By.name("txtBusApePaterno");
    private static final By AP_MATERNO_INPUT = By.name("txtBusApeMaterno");
    private static final By NOMBRES_INPUT = By.name("txtBusNombre");
    private static final By BUSCAR_BUTTON = By.name("BtnConsultar");
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
    public SisvfConsultaNombres(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * Hace clic en el botón de Consultor del módulo SISGEN:
     * guarda la pestaña actual, hace clic en el modal y cambia a la nueva pestaña.
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
     * Hace clic en el botón de "Tipo de búsqueda".
     */
    public void clickTipoDeBusqueda() {
        wait.until(ExpectedConditions.elementToBeClickable(TIPO_DE_BUSQUEDA)).click();
    }

    /**
     * Ingresa los datos de búsqueda (apellido paterno, apellido materno y nombres) y hace clic en buscar.
     *
     * @param apPaterno Apellido paterno.
     * @param apMaterno Apellido materno.
     * @param nombres   Nombres.
     */
    public void enterData(String apPaterno, String apMaterno, String nombres) {
        driver.findElement(AP_PATERNO_INPUT).sendKeys(apPaterno);
        driver.findElement(AP_MATERNO_INPUT).sendKeys(apMaterno);
        driver.findElement(NOMBRES_INPUT).sendKeys(nombres);
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
