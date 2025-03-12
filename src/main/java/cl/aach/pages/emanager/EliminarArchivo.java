package cl.aach.pages.emanager;

import cl.aach.utils.ConfigUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Clase para eliminar archivos en el sistema eManager.
 * Versión adaptada para trabajar con WebDriver proporcionado por BaseTest.
 */
public class EliminarArchivo {

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_EMANAGER = ConfigUtil.getProperty("emanager.url");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // Localizadores para login y navegación
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By BOTON_CONTENIDO = By.id("bot02");
    private static final By BOTON_ARCHIVO = By.xpath("//*[@id=\"menu2\"]/table/tbody/tr[4]/td/a");

    // Localizadores para la eliminación de archivo
    private static final By COLUMNA_TITULO = By.xpath("//table[@class='GridView']//td[contains(text(), 'Este es un archivo de pruebas')]");
    private static final By BOTONES_ELIMINAR = By.xpath("//input[@title='Eliminar']");

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
    public EliminarArchivo(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        this.driverCreatedInternally = false;
    }

    // ==============================
    // Métodos Públicos
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
     * @param usuario Nombre de usuario.
     * @param clave   Contraseña.
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
     * Elimina todos los archivos con el título específico.
     */
    public void eliminarNormativa() {
        while (true) {
            // Buscar elementos con el título específico
            List<WebElement> titulosEncontrados = driver.findElements(COLUMNA_TITULO);

            // Si no hay más elementos con ese título, salir del bucle
            if (titulosEncontrados.isEmpty()) {
                break;
            }

            // Buscar los botones de eliminar
            List<WebElement> botonesEliminarEncontrados = driver.findElements(BOTONES_ELIMINAR);

            // Hacer clic en el primer botón de eliminar, si existe
            if (!botonesEliminarEncontrados.isEmpty()) {
                botonesEliminarEncontrados.get(0).click();

                // Esperar y aceptar la alerta
                try {
                    WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                    Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());
                    alert.accept();

                    // Pequeña pausa para permitir la actualización de la página
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
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