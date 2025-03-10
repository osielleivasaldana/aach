package cl.aach.pages.sgu;

import cl.aach.pages.LoginPage;
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
import java.util.Objects;

public class ValidarEstadoSolicitud {

    // --- Constantes ---
    private static final String URL_SERVER = ConfigUtil.getProperty("server.url");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // --- Localizadores ---
    private static final By BOTON_GESTION_USUARIOS = By.xpath("//*[@id=\"ctl00_tdbarra2\"]/a");
    private static final By BOTON_SOLICITUDES = By.cssSelector("a[title='Solicitudes'][href='SolicitudesUsuarios.aspx']");
    private static final By DETALLE = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_GridView1\"]/tbody/tr[2]/td[1]/input");
    private static final By OPERACION = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_tdDetalle\"]/table/tbody/tr[2]/td/table/tbody/tr[2]/td[4]");
    private static final By SISTEMA = By.xpath("//*[@id=\"menu-primary-menu\"]/li[7]/a");
    private static final By RESULTADO = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[2]/td[2]/div");

    private String operacionLabel;

    // --- Atributos de clase ---
    private WebDriver driver;
    private WebDriverWait wait;

    // --- Constructor ---
    public ValidarEstadoSolicitud() {
        ChromeOptions options = setupChromeOptions();
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    // --- Configuración del navegador ---
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

    // --- Métodos principales ---

    @Step("Abrir URL del sistema")
    public void abrirUrl() {
        driver.get(URL_SERVER);
    }

    @Step("Realizar login en el sistema")
    public void login() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("15231738-P", "Maliche.1981");
    }

    @Step("Acceder a la gestión de solicitudes")
    public void gestionDeSolicitudes() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_GESTION_USUARIOS)));
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_SOLICITUDES)));
        abrirDetalleSolicitud();
        guardarUltimaOperacion();
        volverAMenuSistema();
    }

    @Step("Abrir detalle de la solicitud")
    private void abrirDetalleSolicitud() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(DETALLE)));
    }

    @Step("Guardar el valor de la última operación realizada")
    private void guardarUltimaOperacion() {
        WebElement operacion = wait.until(ExpectedConditions.presenceOfElementLocated(OPERACION));
        operacionLabel = operacion.getText().trim();
        System.out.println("Última operación registrada: " + operacionLabel);
    }

    @Step("Volver al menú SISTEMA")
    private void volverAMenuSistema() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(SISTEMA)));
    }

    @Step("Validar el resultado de la operación")
    public void validarResultado(String resultadoEsperado) {
        System.out.println("El último estado aprobado es: " + operacionLabel);

        if (Objects.equals(operacionLabel, "Activar")) {
            validarElementoPresente(resultadoEsperado);
        } else if (Objects.equals(operacionLabel, "Denegación")) {
            validarElementoNoPresente();
        }
    }

    @Step("Validar que el resultado es el esperado")
    private void validarElementoPresente(String resultadoEsperado) {
        WebElement resultadoElement = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO));
        String resultadoActual = resultadoElement.getText().trim();

        assert resultadoActual.equals(resultadoEsperado) :
                String.format("ERROR: Resultado esperado '%s', pero fue '%s'", resultadoEsperado, resultadoActual);

        System.out.println("✅ Validación exitosa: El resultado esperado coincide con el obtenido.");
    }

    @Step("Validar que el elemento de resultado NO está presente")
    private void validarElementoNoPresente() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO));

            throw new AssertionError("❌ ERROR: El elemento RESULTADO está presente, pero se esperaba que NO estuviera.");
        } catch (TimeoutException e) {
            System.out.println("✅ Validación exitosa: El elemento RESULTADO NO está presente, como se esperaba.");
        }
    }

    // Método para cerrar el WebDriver
    @Step("Cerrar el navegador")
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
