package cl.aach.utils;

import cl.aach.models.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Implementación de la estrategia de login para el Portal principal.
 * Esta es la estrategia por defecto usada en la mayoría de los tests.
 */
public class PortalLoginStrategy implements LoginStrategy {

    private final TestData testData;

    // Localizadores para el login del Portal
    private static final By USUARIO_INPUT = By.id("ctl00_body_txUsuario");
    private static final By CLAVE_INPUT = By.id("ctl00_body_txPassword");
    private static final By BOTON_INGRESAR = By.xpath("//*[@id=\"aspnetForm\"]/div[4]/div/div/main/section/div[2]/div[1]/div/div[3]/div/button");
    private static final By ELEMENTO_POST_LOGIN = By.xpath("//*[@id=\"ctl00_tdbarra1\"]/img");

    /**
     * Constructor que recibe los datos de prueba necesarios para el login.
     *
     * @param testData datos de prueba con credenciales
     */
    public PortalLoginStrategy(TestData testData) {
        this.testData = testData;
    }

    @Override
    public String getBaseUrl() {
        return ConfigUtil.getProperty("server.url"); // Esto debe ser https://usuarios.aachtest.cl/App/Inicio.aspx en config.properties
    }

    @Override
    public boolean performLogin(WebDriver driver) {
        try {
            System.out.println("Realizando login en Portal con usuario: " + testData.credentials.portalUser.user);
            driver.get(getBaseUrl());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Esperar a que aparezca el campo de usuario
            WebElement usuarioInput = wait.until(ExpectedConditions.visibilityOfElementLocated(USUARIO_INPUT));
            usuarioInput.clear();
            usuarioInput.sendKeys(testData.credentials.portalUser.user);
            System.out.println("Usuario ingresado correctamente");

            // Ingresar contraseña
            WebElement claveInput = driver.findElement(CLAVE_INPUT);
            claveInput.clear();
            claveInput.sendKeys(testData.credentials.portalUser.password);
            System.out.println("Contraseña ingresada correctamente");

            // Hacer clic en el botón de ingreso
            WebElement botonIngresar = wait.until(ExpectedConditions.elementToBeClickable(BOTON_INGRESAR));
            botonIngresar.click();
            System.out.println("Clic en botón de ingreso realizado");

            // Esperar a que aparezca un elemento que indique login exitoso
            wait.until(ExpectedConditions.visibilityOfElementLocated(ELEMENTO_POST_LOGIN));
            System.out.println("Login en Portal exitoso - Elemento post-login detectado");

            return true;
        } catch (Exception e) {
            System.err.println("Error en login de Portal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isSessionActive(WebDriver driver) {
        try {
            // Verificar si existe el elemento que aparece después del login
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.visibilityOfElementLocated(ELEMENTO_POST_LOGIN));
            System.out.println("Sesión de Portal activa detectada");
            return true;
        } catch (Exception e) {
            System.out.println("No se detectó sesión activa de Portal");
            return false;
        }
    }

    @Override
    public String getStrategyName() {
        return "Portal Standard Login";
    }
}