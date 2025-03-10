package cl.aach.pages.emanager360;

import cl.aach.utils.ConfigUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModificarNorma {

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_EMANAGER = ConfigUtil.getProperty("emanager.url");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // ==============================
    // Localizadores
    // ==============================
    // Login y navegación
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By BOTON_CONTENIDO = By.id("bot05");
    private static final By BOTON_ARCHIVO = By.xpath("//*[@id=\"menu5\"]/table/tbody/tr[2]/td/a");

    // Localizadores de la tabla y opciones de normativa
    private static final By TABLA_NORMATIVA = By.className("GridView");
    private static final By COLUMNA_NOMBRE = By.xpath(".//tr/td[3]");
    private static final By BOTON_MODIFICAR = By.xpath(".//a/img[@alt='Modificar']");
    private static final By SELECT_ESTADO = By.name("ctl00$ContentPlaceHolder1$Estado");
    private static final By BOTON_MODIFICAR_NORMA = By.name("ctl00$ContentPlaceHolder1$Button1");
    private static final By BOTON_VOLVER_A_TABLA = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_tbForm\"]/tbody/tr[1]/td/a[1]");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // Constructor
    // ==============================
    public ModificarNorma() {
        ChromeOptions options = configurarOpcionesChromeDriver();
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    // ==============================
    // Configuración del WebDriver
    // ==============================
    private ChromeOptions configurarOpcionesChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--disable-blink-features=AutomationControlled",
                "--disable-extensions",
                "--no-sandbox",
                "--disable-popup-blocking",
                "--start-maximized",
                "--disable-infobars",
                "--disable-browser-side-navigation",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-features=IsolateOrigins,site-per-process"
        );

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("useAutomationExtension", false);

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
        wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_ARCHIVO)).click();
    }

    /**
     * Modifica la normativa generada automáticamente cambiando su estado.
     *
     * @param nuevoEstado El nuevo estado a asignar.
     * @return true si la modificación fue exitosa, false en caso contrario.
     */
    public boolean modificarNormativaAutomatica(String nuevoEstado) {
        try {
            WebElement filaNormativa = encontrarFilaNormativa("NORMATIVA GENERADA AUTOMÁTICAMENTE");
            modificarEstadoNormativa(filaNormativa, nuevoEstado);
            return verificarEstadoNormativa("NORMATIVA GENERADA AUTOMÁTICAMENTE", nuevoEstado);
        } catch (Exception e) {
            System.err.println("Error al modificar normativa: " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // Métodos Auxiliares
    // ==============================
    /**
     * Busca y retorna la fila de la tabla que contenga la normativa especificada.
     *
     * @param textoNormativa Texto que identifica la normativa.
     * @return La fila (elemento Web) encontrada.
     * @throws RuntimeException si no se encuentra la normativa.
     */
    private WebElement encontrarFilaNormativa(String textoNormativa) {
        WebElement tabla = wait.until(ExpectedConditions.presenceOfElementLocated(TABLA_NORMATIVA));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));

        for (WebElement fila : filas) {
            List<WebElement> celdas = fila.findElements(By.tagName("td"));
            if (!celdas.isEmpty() && celdas.get(2).getText().contains(textoNormativa)) {
                return fila;
            }
        }
        throw new RuntimeException("No se encontró normativa con el texto: " + textoNormativa);
    }

    /**
     * Modifica el estado de la normativa especificada.
     *
     * @param fila        La fila que contiene la normativa a modificar.
     * @param nuevoEstado El nuevo estado a seleccionar.
     */
    private void modificarEstadoNormativa(WebElement fila, String nuevoEstado) {
        fila.findElement(BOTON_MODIFICAR).click();

        WebElement dropdownEstado = wait.until(ExpectedConditions.elementToBeClickable(SELECT_ESTADO));
        new Select(dropdownEstado).selectByVisibleText(nuevoEstado);

        wait.until(ExpectedConditions.elementToBeClickable(BOTON_MODIFICAR_NORMA)).click();
        wait.until(ExpectedConditions.elementToBeClickable(BOTON_VOLVER_A_TABLA)).click();
    }

    /**
     * Verifica que la normativa especificada tenga el estado esperado.
     *
     * @param textoNormativa Texto identificador de la normativa.
     * @param estadoEsperado Estado esperado.
     * @return true si el estado coincide, false de lo contrario.
     * @throws RuntimeException si no se encuentra la normativa.
     */
    private boolean verificarEstadoNormativa(String textoNormativa, String estadoEsperado) {
        WebElement tabla = wait.until(ExpectedConditions.presenceOfElementLocated(TABLA_NORMATIVA));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));

        for (WebElement fila : filas) {
            List<WebElement> celdas = fila.findElements(By.tagName("td"));
            if (!celdas.isEmpty() && celdas.get(2).getText().contains(textoNormativa)) {
                String estadoActual = celdas.get(6).getText();
                return estadoEsperado.equals(estadoActual);
            }
        }
        throw new RuntimeException("No se encontró normativa para verificar su estado.");
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
