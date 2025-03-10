package cl.aach.pages.emanager360;

import cl.aach.utils.ConfigUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EliminarNorma {

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_EMANAGER = ConfigUtil.getProperty("emanager.url");
    private static final String NORMATIVA_TEXT = "NORMATIVA GENERADA AUTOMÁTICAMENTE";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // ==============================
    // Localizadores
    // ==============================
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By BOTON_CONTENIDO = By.id("bot05");
    private static final By BOTON_ARCHIVO = By.xpath("//*[@id=\"menu5\"]/table/tbody/tr[2]/td/a");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // Constructor
    // ==============================
    public EliminarNorma() {
        ChromeOptions options = setupChromeOptions();
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    // ==============================
    // Configuración del navegador
    // ==============================
    private ChromeOptions setupChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-extensions", "--no-sandbox", "--disable-popup-blocking");
        options.addArguments("--start-maximized", "--disable-infobars", "--disable-browser-side-navigation");
        options.addArguments("--disable-dev-shm-usage", "--disable-gpu");
        options.addArguments("--disable-features=IsolateOrigins,site-per-process");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        // Agregar un directorio único para user-data-dir
        try {
            String uniqueProfile = Files.createTempDirectory("chrome_profile_" + UUID.randomUUID()).toString();
            options.addArguments("--user-data-dir=" + uniqueProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return options;
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
     * @param usuario Usuario a ingresar.
     * @param clave   Contraseña a ingresar.
     */
    public void login(String usuario, String clave) {
        wait.until(ExpectedConditions.presenceOfElementLocated(USUARIO_INPUT)).sendKeys(usuario);
        driver.findElement(CLAVE_INPUT).sendKeys(clave);
        driver.findElement(BOTON_INGRESAR).click();
    }

    /**
     * Navega al menú de archivos.
     */
    public void clickMenu() {
        WebElement contenidoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_CONTENIDO));
        new Actions(driver).moveToElement(contenidoElement).perform();
        WebElement archivoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_ARCHIVO));
        archivoElement.click();
    }

    /**
     * Elimina normativas que contengan el texto especificado.
     * Se eliminan las normativas mientras se encuentren en la página.
     */
    public void eliminarNormativa() {
        while (true) {
            try {
                // Buscar la fila que contiene el texto de la normativa
                WebElement normativaRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[contains(text(), '" + NORMATIVA_TEXT + "')]")));

                // Buscar el botón de modificar asociado a la fila encontrada y hacer clic
                WebElement modifyButton = normativaRow.findElement(
                        By.xpath(".//ancestor::tr/td[2]/a[img[@alt='Modificar']]"));
                modifyButton.click();

                // Buscar y hacer clic en el botón de eliminar
                WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//img[@alt='Eliminar']")));
                deleteButton.click();

                // Manejar las alertas
                aceptarAlertas();
            } catch (TimeoutException e) {
                System.out.println("No se encontraron más normativas con el texto: " + NORMATIVA_TEXT);
                break;
            } catch (Exception e) {
                System.err.println("Ocurrió un error al eliminar la normativa: " + e.getMessage());
                break;
            }
        }
    }

    // ==============================
    // Métodos Auxiliares
    // ==============================

    /**
     * Acepta las alertas que aparecen durante la eliminación de la normativa.
     */
    private void aceptarAlertas() {
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept(); // Aceptar primera alerta
            wait.until(ExpectedConditions.alertIsPresent()).accept(); // Aceptar segunda alerta
        } catch (NoAlertPresentException e) {
            System.err.println("No se encontraron alertas al eliminar la normativa.");
        }
    }

    /**
     * Cierra el navegador.
     */
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
