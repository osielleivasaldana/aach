package cl.aach.pages.srcei;

import cl.aach.utils.ClickUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Set;

public class SrciRvmEnLocal {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[7]/td[2]/div[2]");
    private static final By MODAL_BUTTON = By.xpath("//*[@id=\"tdMensajes\"]/table/tbody/tr[2]/td/input");
    private static final By MENU_BUTTON = By.xpath("//*[@id=\"menuSM\"]/li[3]/a");
    private static final By SUB_MENU_BUTTON = By.xpath("//*[@id=\"menuSM\"]/li[3]/div/a[1]");
    private static final By BUSCAR_EN_LINEA_BUTTON = By.id("masterContenido_btnBusquedaLinea");
    private static final By RVM_INPUT = By.xpath("//*[@id=\"masterContenido_txtRvm\"]");
    private static final By BUSCAR_BUTTON = By.id("masterContenido_btnBuscarLocal");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"masterContenido_lblAACHRVM\"]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private String originalTab;

    // ==============================
    // Constructor
    // ==============================
    public SrciRvmEnLocal(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * Hace clic en el botón del consultor del módulo SISGEN, interactúa con el modal y cambia a la nueva pestaña.
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
     * Navega al menú y selecciona el submenú correspondiente.
     */
    public void clickMenu() {
        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(MENU_BUTTON));
        new Actions(driver).moveToElement(menu).perform();
        WebElement subMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(SUB_MENU_BUTTON));
        subMenu.click();
    }

    /**
     * Hace clic en el botón "Buscar en Línea".
     */
    public void clickBuscarEnLinea() {
        wait.until(ExpectedConditions.elementToBeClickable(BUSCAR_EN_LINEA_BUTTON)).click();
    }

    /**
     * Ingresa el RVM en el campo correspondiente y hace clic en el botón buscar.
     *
     * @param rvm El RVM a buscar.
     */
    public void enterRvm(String rvm) {
        driver.findElement(RVM_INPUT).sendKeys(rvm);
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

    /**
     * Cierra la pestaña actual y vuelve a la pestaña original.
     */
    public void closeAndReturnToOriginalTab() {
        driver.close();
        driver.switchTo().window(originalTab);
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
