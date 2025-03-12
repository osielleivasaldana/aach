package cl.aach.pages.emanager;

import cl.aach.utils.ConfigUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Clase para validar archivos en el sistema eManager.
 * Versión adaptada para trabajar con WebDriver proporcionado por BaseTest.
 */
public class ValidarArchivo {

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_EMANAGER = ConfigUtil.getProperty("emanager.url");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // ==============================
    // Localizadores
    // ==============================
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By BOTON_CONTENIDO = By.id("bot02");
    private static final By BOTON_ARCHIVO = By.xpath("//*[@id=\"menu2\"]/table/tbody/tr[4]/td/a");
    private static final By COLUMNA_TITULO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_GridView1\"]/tbody/tr[2]/td[6]");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private boolean driverCreatedInternally;

    // ==============================
    // Constructores
    // ==============================

    /**
     * Constructor que recibe un WebDriver ya configurado.
     * Este es el constructor que se usará con la nueva arquitectura de BaseTest.
     *
     * @param driver WebDriver previamente configurado e inicializado.
     */
    public ValidarArchivo(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        this.driverCreatedInternally = false;
    }

    // ==============================
    // Métodos Principales
    // ==============================

    /**
     * Abre la URL del servicio eManager.
     */
    public void abrirUrl() {
        driver.get(URL_EMANAGER);
    }

    /**
     * Realiza el login en el servicio.
     *
     * @param usuario El nombre de usuario.
     * @param clave   La contraseña.
     */
    public void login(String usuario, String clave) {
        wait.until(ExpectedConditions.presenceOfElementLocated(USUARIO_INPUT)).sendKeys(usuario);
        driver.findElement(CLAVE_INPUT).sendKeys(clave);
        driver.findElement(BOTON_INGRESAR).click();
    }

    /**
     * Navega al menú de archivo.
     */
    public void clickMenu() {
        WebElement consultasElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_CONTENIDO));
        new Actions(driver).moveToElement(consultasElement).perform();
        WebElement archivosElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_ARCHIVO));
        archivosElement.click();
    }

    /**
     * Valida el resultado comparando el texto del elemento con el texto esperado.
     *
     * @param expectedText El texto esperado.
     */
    public void validarResultado(String expectedText) {
        try {
            // Espera a que el elemento esté presente
            WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(COLUMNA_TITULO));
            String actualText = resultLabel.getText().trim();

            // Realiza el assert para comparar los textos
            assert actualText.equals(expectedText) :
                    "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";

            System.out.println("Validación exitosa: el texto es el esperado -> " + actualText);
        } catch (TimeoutException e) {
            throw new AssertionError("El elemento esperado no fue encontrado en la página. Verifica que exista el elemento con el localizador proporcionado.", e);
        } catch (NoSuchElementException e) {
            throw new AssertionError("El elemento no existe en el DOM. Revisa el localizador utilizado.", e);
        }
    }

    /**
     * Cierra el navegador.
     * Este método solo cerrará el navegador si fue creado internamente por esta clase.
     */
    public void close() {
        if (driver != null && driverCreatedInternally) {
            driver.quit();
        }
    }
}