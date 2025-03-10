package cl.aach.pages.svm;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SvmPage {

    // ==============================
    // Constantes
    // ==============================
    private static final String SERVICE_URL = "https://svm.aachpre.cl/wsSvmConsultaBono/wsSvmConsultaBono.asmx?op=ConsultaBonoJson";

    // ==============================
    // Localizadores
    // ==============================
    private static final By USUARIO_INPUT = By.name("Usuario");
    private static final By CLAVE_INPUT = By.name("Clave");
    private static final By BONO_INPUT = By.name("Bono");
    private static final By INVOKE_BUTTON = By.cssSelector("input[type='submit'][value='Invoke']");
    private static final By RESPONSE_ELEMENT = By.tagName("pre");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // Constructor
    // ==============================
    public SvmPage() {
        // Configurar WebDriverManager y crear instancia de ChromeDriver
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver();
        // Configurar las esperas explícitas
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * Abre la URL del servicio.
     */
    public void openServiceUrl() {
        driver.get(SERVICE_URL);
    }

    /**
     * Ingresa los datos en el formulario y envía la solicitud.
     *
     * @param usuario Usuario a ingresar.
     * @param clave   Contraseña a ingresar.
     * @param bono    Bono a consultar.
     */
    public void ingresarDatos(String usuario, String clave, String bono) {
        wait.until(ExpectedConditions.presenceOfElementLocated(USUARIO_INPUT)).sendKeys(usuario);
        driver.findElement(CLAVE_INPUT).sendKeys(clave);
        driver.findElement(BONO_INPUT).sendKeys(bono);
        driver.findElement(INVOKE_BUTTON).click();
    }

    /**
     * Obtiene y retorna la respuesta del servicio.
     *
     * @return Texto de la respuesta.
     */
    public String validarRespuesta() {
        wait.until(ExpectedConditions.presenceOfElementLocated(RESPONSE_ELEMENT));
        return driver.findElement(RESPONSE_ELEMENT).getText();
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
