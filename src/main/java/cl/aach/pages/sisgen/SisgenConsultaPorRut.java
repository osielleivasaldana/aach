package cl.aach.pages.sisgen;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SisgenConsultaPorRut {

    // ==============================
    // Constantes - Localizadores
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[6]/td[2]/div[1]");
    private static final By MENU_BUTTON = By.xpath("//*[@id=\"menuSM\"]/li[5]/a");
    private static final By SUB_MENU_BUTTON = By.xpath("//*[@id=\"menuSM\"]/li[5]/div/a[1]");
    private static final By RUT_INPUT = By.xpath("//*[@id=\"masterContenido_txtRut\"]");
    private static final By BUSCAR_BUTTON = By.id("masterContenido_btnLnkBuscar");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"masterContenido_GridResultado\"]/tbody/tr[2]/td[2]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public SisgenConsultaPorRut(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * clickConsultor es el encargado de iniciar el sistema en el lanzador de aplicaciones.
     * Captura y maneja alertas que puedan aparecer (por ejemplo, alertas de VPN).
     *
     * @return String - Mensaje de alerta capturado (si existe) o null si no hay alerta
     */
    public String clickConsultor() {
        String alertMessage = null;
        tabManager.saveCurrentTab(); // Guarda la pestaña actual antes de abrir una nueva

        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
        boton.click();

        try {
            // Configuramos un wait corto específicamente para alertas
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            alertWait.until(ExpectedConditions.alertIsPresent());

            // Si llegamos aquí, existe una alerta
            alertMessage = driver.switchTo().alert().getText();
            System.out.println("⚠️ Alerta detectada: " + alertMessage);

            // Aceptamos la alerta
            driver.switchTo().alert().accept();

            // Como apareció una alerta, no cambiamos de pestaña ya que probablemente falló la apertura
            System.out.println("❌ No se pudo abrir el módulo. Posible causa: Se requiere VPN");
            return alertMessage;
        } catch (Exception e) {
            // No hay alerta, continuamos con el flujo normal
            System.out.println("✅ Módulo abierto correctamente");
        }

        // Si no hubo alerta, cambiamos a la nueva pestaña
        tabManager.switchToNewTab();
        return alertMessage;
    }

    /**
     * Navega al menú de consultas y selecciona el submenú correspondiente.
     */
    public void clickMenu() {
        WebElement consultasElement = wait.until(ExpectedConditions.visibilityOfElementLocated(MENU_BUTTON));
        Actions actions = new Actions(driver);
        actions.moveToElement(consultasElement).perform();
        WebElement subMenuElement = wait.until(ExpectedConditions.visibilityOfElementLocated(SUB_MENU_BUTTON));
        subMenuElement.click();
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
}
