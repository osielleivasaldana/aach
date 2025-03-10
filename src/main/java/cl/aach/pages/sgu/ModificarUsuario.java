package cl.aach.pages.sgu;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.ConfigUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
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

    // ==============================
    // Constructor
    // ==============================
    public ModificarUsuario() {
        ChromeOptions options = setupChromeOptions();
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
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
     * Navega al submenú de gestión de usuarios.
     */
    public void clickSubMenu() {
        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_GESTION_USUARIOS));
        ClickUtils.click(driver, boton);
    }

    /**
     * Busca un usuario por RUT.
     *
     * @param rut El RUT del usuario a buscar.
     */
    public void buscarUsuario(String rut) {
        wait.until(ExpectedConditions.presenceOfElementLocated(RUT_INPUT)).sendKeys(rut);
        driver.findElement(BOTON_CONSULTAR).click();
    }

    /**
     * Realiza la modificación del cargo del usuario.
     *
     * @param cargo El nuevo cargo a asignar.
     */
    public void realizarModificacion(String cargo) {
        WebElement modificar = wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_MODIFICAR));
        ClickUtils.click(driver, modificar);
        WebElement campoModificar = wait.until(ExpectedConditions.presenceOfElementLocated(CAMPO_A_MODIFICAR));
        campoModificar.clear();
        campoModificar.sendKeys(cargo);
        driver.findElement(BOTON_MODIFICAR_CAMPO).click();
    }

    /**
     * Valida que el resultado de la modificación sea el esperado.
     *
     * @param mensaje El mensaje esperado.
     */
    public void validarResultado(String mensaje) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO));
        String actualText = resultLabel.getText();
        if (actualText.equals(mensaje)) {
            System.out.println("Validación exitosa: El texto coincide con el esperado: '" + actualText + "'");
        } else {
            String errorMessage = "Texto esperado: '" + mensaje + "', pero fue: '" + actualText + "'";
            System.err.println(errorMessage);
            throw new AssertionError(errorMessage);
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
