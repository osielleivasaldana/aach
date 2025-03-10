package cl.aach.pages.srcei;

import cl.aach.utils.ClickUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Set;

public class SrciRutEnLinea {

    private WebDriver driver;
    private WebDriverWait wait;
    private String originalTab;

    // Localizadores
    private static final By CONSULTOR_BUTTON = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[7]/td[2]/div[2]");
    private static final By MODAL_BUTTON = By.xpath("//*[@id=\"tdMensajes\"]/table/tbody/tr[2]/td/input");
    private static final By MENU_BUTTON = By.xpath("//*[@id=\"menuSM\"]/li[4]/a");
    private static final By SUB_MENU_BUTTON = By.xpath("//*[@id=\"menuSM\"]/li[4]/div/a[1]");
    private static final By BUSCAR_EN_LINEA_BUTTON = By.id("masterContenido_btnBusquedaLinea");
    private static final By RUT_INPUT = By.xpath("//*[@id=\"masterContenido_txtRun\"]");
    private static final By BUSCAR_BUTTON = By.id("masterContenido_btnBuscar");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"masterContenido_GridResultado\"]/tbody/tr[1]/th[2]");

    public SrciRutEnLinea(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Step("Abrir módulo SISGEN haciendo clic en el botón Administrador")
    public void abrirModuloSISGEN() {
        guardarPestanaActual();
        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
        ClickUtils.click(driver, boton);

        WebElement modal = wait.until(ExpectedConditions.presenceOfElementLocated(MODAL_BUTTON));
        ClickUtils.click(driver, modal);
        cambiarANuevaPestana();
    }

    @Step("Navegar al menú de consultas y seleccionar siniestros")
    public void navegarAMenuSiniestros() {
        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(MENU_BUTTON));
        new Actions(driver).moveToElement(menu).perform();

        WebElement subMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(SUB_MENU_BUTTON));
        subMenu.click();
    }

    @Step("Guardar la pestaña actual")
    private void guardarPestanaActual() {
        originalTab = driver.getWindowHandle();
    }

    @Step("Cambiar a la nueva pestaña")
    private void cambiarANuevaPestana() {
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        for (String tab : driver.getWindowHandles()) {
            if (!tab.equals(originalTab)) {
                driver.switchTo().window(tab);
                break;
            }
        }
    }

    @Step("Cerrar la pestaña actual y volver a la pestaña original")
    public void cerrarYRegresarPestana() {
        driver.close();
        driver.switchTo().window(originalTab);
    }

    @Step("Hacer clic en el botón 'Buscar en Línea'")
    public void buscarEnLinea() {
        wait.until(ExpectedConditions.elementToBeClickable(BUSCAR_EN_LINEA_BUTTON)).click();
    }

    @Step("Ingresar el RUT: {0} y buscar")
    public void buscarPorRut(String rut) {
        wait.until(ExpectedConditions.presenceOfElementLocated(RUT_INPUT)).sendKeys(rut);
        driver.findElement(BUSCAR_BUTTON).click();
    }

    @Step("Validar el resultado esperado: {0}")
    public void validarResultado(String textoEsperado) {
        WebElement resultado = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO_LABEL));
        String textoReal = resultado.getText();

        assert textoReal.equals(textoEsperado) :
                String.format("El resultado no coincide. Esperado: '%s', Actual: '%s'", textoEsperado, textoReal);
    }
}
