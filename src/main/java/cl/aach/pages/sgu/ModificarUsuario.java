package cl.aach.pages.sgu;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.ConfigUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ModificarUsuario {

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_SGU = ConfigUtil.getProperty("sgu.url");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_WAIT_MS = 1000;

    // ==============================
    // Localizadores
    // ==============================
    private static final By USUARIO_INPUT = By.id("loginUsuario");
    private static final By CLAVE_INPUT = By.id("loginContrasenya");
    private static final By BOTON_INGRESAR = By.xpath("//*[@id=\"tb\"]/tbody/tr[3]/td/center/div/div[2]/div[4]/button");
    private static final By BOTON_GESTION_USUARIOS = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_AccAdministrador\"]/div/a[1]");
    private static final By RUT_INPUT = By.name("ctl00$ContentPlaceHolder1$Usuario");
    private static final By BOTON_CONSULTAR = By.id("btnConsultar");
    private static final By BOTON_MODIFICAR = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_GridView1\"]/tbody/tr[2]/td[2]/input");
    private static final By CAMPO_A_MODIFICAR = By.name("ctl00$ContentPlaceHolder1$Cargo");
    private static final By BOTON_MODIFICAR_CAMPO = By.name("ctl00$ContentPlaceHolder1$btnAgregar2");
    private static final By RESULTADO = By.id("ctl00_ContentPlaceHolder1_AlertaAnt");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private boolean isDriverOwner; // Flag para saber si esta clase creó el driver

    // ==============================
    // Constructor con WebDriver existente (para usar con BaseTest)
    // ==============================
    public ModificarUsuario(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        this.isDriverOwner = false; // No somos dueños del driver
    }

    // ==============================
    // Constructor
    // ==============================
    public ModificarUsuario() {
        ChromeOptions options = setupChromeOptions();
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        this.isDriverOwner = true; // Somos dueños del driver
    }

    // ==============================
    // Configuración del Navegador
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

        return options;
    }

    // ==============================
    // Métodos Principales
    // ==============================

    /**
     * Abre la URL del servicio SGU.
     */
    public void abrirUrl() {
        driver.get(URL_SGU);
    }

    /**
     * Realiza el login en el sistema SGU.
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
     * Método mejorado para hacer clic con manejo de elementos obsoletos
     *
     * @param by Localizador del elemento
     * @return true si el clic fue exitoso, false en caso contrario
     */
    private boolean clickWithRetry(By by) {
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                // Espera a que el elemento sea clickeable
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

                // Intenta primero con un clic normal
                try {
                    element.click();
                    return true;
                } catch (Exception e) {
                    // Si falla, intenta con JavascriptExecutor
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].scrollIntoView(true);", element);
                    js.executeScript("arguments[0].click();", element);
                    return true;
                }
            } catch (StaleElementReferenceException e) {
                retryCount++;
                System.out.println("Elemento obsoleto detectado, reintento " + retryCount);

                // Espera antes de reintentar
                try {
                    Thread.sleep(RETRY_WAIT_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

            } catch (Exception e) {
                retryCount++;
                System.err.println("Error al hacer clic: " + e.getMessage());

                // Espera antes de reintentar
                try {
                    Thread.sleep(RETRY_WAIT_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.err.println("No se pudo hacer clic después de " + MAX_RETRIES + " intentos");
        return false;
    }

    /**
     * Navega al submenú de gestión de usuarios.
     */
    public void clickSubMenu() {
        // Espera a que la página cargue completamente después del login
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Usa el método mejorado para hacer clic
        if (!clickWithRetry(BOTON_GESTION_USUARIOS)) {
            throw new RuntimeException("No se pudo hacer clic en el botón de gestión de usuarios");
        }
    }

    /**
     * Busca un usuario por RUT.
     *
     * @param rut El RUT del usuario a buscar.
     */
    public void buscarUsuario(String rut) {
        // Asegúrate de que la página ha cargado
        wait.until(ExpectedConditions.presenceOfElementLocated(RUT_INPUT));

        // Limpia cualquier texto que pudiera estar presente
        WebElement rutInput = driver.findElement(RUT_INPUT);
        rutInput.clear();
        rutInput.sendKeys(rut);

        // Usa el método mejorado para hacer clic
        if (!clickWithRetry(BOTON_CONSULTAR)) {
            throw new RuntimeException("No se pudo hacer clic en el botón de consultar");
        }
    }

    /**
     * Realiza la modificación del cargo del usuario.
     *
     * @param cargo El nuevo cargo a asignar.
     */
    public void realizarModificacion(String cargo) {
        // Espera a que los resultados de la búsqueda se carguen
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Usa el método mejorado para hacer clic en el botón modificar
        if (!clickWithRetry(BOTON_MODIFICAR)) {
            throw new RuntimeException("No se pudo hacer clic en el botón de modificar");
        }

        // Espera a que el formulario de modificación se cargue
        WebElement campoModificar = wait.until(ExpectedConditions.presenceOfElementLocated(CAMPO_A_MODIFICAR));
        campoModificar.clear();
        campoModificar.sendKeys(cargo);

        // Usa el método mejorado para hacer clic en el botón de guardar modificación
        if (!clickWithRetry(BOTON_MODIFICAR_CAMPO)) {
            throw new RuntimeException("No se pudo hacer clic en el botón de guardar modificación");
        }
    }

    /**
     * Valida que el resultado de la modificación sea el esperado.
     *
     * @param mensaje El mensaje esperado.
     */
    public void validarResultado(String mensaje) {
        // Espera más tiempo para el mensaje de resultado
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            WebElement resultLabel = longWait.until(ExpectedConditions.visibilityOfElementLocated(RESULTADO));
            String actualText = resultLabel.getText();
            if (actualText.equals(mensaje)) {
                System.out.println("Validación exitosa: El texto coincide con el esperado: '" + actualText + "'");
            } else {
                String errorMessage = "Texto esperado: '" + mensaje + "', pero fue: '" + actualText + "'";
                System.err.println(errorMessage);
                throw new AssertionError(errorMessage);
            }
        } catch (StaleElementReferenceException e) {
            // Si el elemento se vuelve obsoleto, intenta de nuevo
            System.out.println("El elemento de resultado se volvió obsoleto, reintentando...");
            // Espera un momento antes de reintentar
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            validarResultado(mensaje); // Reintentar recursivamente
        }
    }

    /**
     * Cierra el WebDriver.
     */
    public void close() {
        // Solo cerramos el driver si lo creamos en esta clase
        if (driver != null && isDriverOwner) {
            driver.quit();
            driver = null;
        }
    }
}