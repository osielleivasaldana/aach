package cl.aach.pages.emanager;

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

public class ValidarArchivo {

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_EMANAGER = ConfigUtil.getProperty("emanager.url");

    // Localizadores
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By BOTON_CONTENIDO = By.id("bot02");
    private static final By BOTON_ARCHIVO = By.xpath("//*[@id=\"menu2\"]/table/tbody/tr[4]/td/a");
    private static final By COLUMNA_TITULO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_GridView1\"]/tbody/tr[2]/td[6]");

    // ==============================
    // Constructor
    // ==============================
    public ValidarArchivo() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
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

        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
        Actions actions = new Actions(driver);
        actions.moveToElement(consultasElement).perform();
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
     * Cierra el WebDriver.
     */
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
