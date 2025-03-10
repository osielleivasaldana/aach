package cl.aach.pages.sgu;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.ConfigUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class NuevaSolicitud {

    // ==============================
    // Constantes y Localizadores
    // ==============================
    private static final String URL_SGU = ConfigUtil.getProperty("sgu.url");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private static final By USUARIO_INPUT = By.id("loginUsuario");
    private static final By CLAVE_INPUT = By.id("loginContrasenya");
    private static final By BOTON_INGRESAR = By.xpath("//*[@id=\"tb\"]/tbody/tr[3]/td/center/div/div[2]/div[4]/button");
    private static final By BOTON_GESTION_USUARIOS = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_AccAdministrador\"]/div/a[1]");
    private static final By RUT_INPUT = By.name("ctl00$ContentPlaceHolder1$Usuario");
    private static final By BOTON_CONSULTAR = By.id("btnConsultar");
    private static final By BOTON_INGRESAR_SOLICITUD = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_GridView1\"]/tbody/tr[2]/td[1]/input");
    private static final By CHECKBOX_SOLICITUD = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_dvForm2\"]/center/table/tbody/tr[3]/td/table/tbody/tr[17]/td/table/tbody/tr/td[3]/input");
    private static final By ESTADO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_dvForm3\"]/div/table/tbody/tr/td/div/table/tbody/tr[2]/td/table/tbody/tr[2]/td[3]/span");
    private static final By BOTON_SIGUIENTE = By.name("btnSiguiente");
    private static final By OBSERVACIONES_INPUT = By.name("Observaciones");
    private static final By BOTON_FINALIZAR = By.name("btnFinalizar");
    private static final By RESULTADO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_dvResultado\"]/div/span");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // Constructor y Configuración del Navegador
    // ==============================
    public NuevaSolicitud() {
        ChromeOptions options = setupChromeOptions();
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    private ChromeOptions setupChromeOptions() {
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
        return options;
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    @Step("Abrir URL de SGU")
    public void abrirUrl() {
        driver.get(URL_SGU);
    }

    @Step("Iniciar sesión con usuario: {0}")
    public void login(String usuario, String clave) {
        wait.until(ExpectedConditions.presenceOfElementLocated(USUARIO_INPUT)).sendKeys(usuario);
        driver.findElement(CLAVE_INPUT).sendKeys(clave);
        driver.findElement(BOTON_INGRESAR).click();
    }

    @Step("Acceder al submenú de gestión de usuarios")
    public void clickSubMenu() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_GESTION_USUARIOS)));
    }

    @Step("Buscar usuario con RUT: {0}")
    public void buscarUsuario(String rut) {
        wait.until(ExpectedConditions.presenceOfElementLocated(RUT_INPUT)).sendKeys(rut);
        driver.findElement(BOTON_CONSULTAR).click();
    }

    @Step("Seleccionar solicitud DRP Pruebas")
    public void especificarSolicitud() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_INGRESAR_SOLICITUD)));
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(CHECKBOX_SOLICITUD)));
        driver.findElement(BOTON_SIGUIENTE).click();
    }

    @Step("Enviar solicitud con observaciones: {0}")
    public void enviarSolicitud(String observaciones) {
        wait.until(ExpectedConditions.presenceOfElementLocated(OBSERVACIONES_INPUT)).sendKeys(observaciones);
        verificarEstadoSolicitud();
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_FINALIZAR)));
        manejarAlerta();
    }

    @Step("Verificar estado de la solicitud")
    private void verificarEstadoSolicitud() {
        WebElement estadoElement = wait.until(ExpectedConditions.presenceOfElementLocated(ESTADO));
        String estadoTexto = estadoElement.getText().trim();
        if ("Denegar".equalsIgnoreCase(estadoTexto)) {
            System.out.println("Se envió la solicitud de denegar el acceso a DRP Pruebas");
        } else if ("Activar".equalsIgnoreCase(estadoTexto)) {
            System.out.println("Se envió la solicitud de Activar el acceso a DRP Pruebas");
        }
    }

    @Step("Manejar alerta de confirmación")
    private void manejarAlerta() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException ignored) {
            // No se encontró alerta, continuar
        }
    }

    @Step("Validar resultado esperado: {0}")
    public void validarResultado(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO));
        String actualText = resultLabel.getText();
        assert actualText.equals(expectedText) : "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }

    @Step("Cerrar el navegador")
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
