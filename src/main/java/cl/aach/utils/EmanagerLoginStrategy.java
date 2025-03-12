package cl.aach.utils;

import cl.aach.models.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Implementación de la estrategia de login para el sistema eManager.
 */
public class EmanagerLoginStrategy implements LoginStrategy {

    private final TestData testData;

    // Localizadores para el login de eManager
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By ELEMENT_AFTER_LOGIN = By.id("bot02"); // Elemento presente después de login exitoso

    /**
     * Constructor que recibe los datos de prueba necesarios para el login.
     *
     * @param testData datos de prueba con credenciales
     */
    public EmanagerLoginStrategy(TestData testData) {
        this.testData = testData;
    }

    @Override
    public String getBaseUrl() {
        return ConfigUtil.getProperty("emanager.url");
    }

    @Override
    public boolean performLogin(WebDriver driver) {
        try {
            System.out.println("Realizando login en eManager con usuario: " + testData.credentials.emanager.user);
            driver.get(getBaseUrl());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Aumentamos el timeout

            WebElement usuarioInput = wait.until(ExpectedConditions.visibilityOfElementLocated(USUARIO_INPUT));
            usuarioInput.clear();
            usuarioInput.sendKeys(testData.credentials.emanager.user);
            System.out.println("Usuario ingresado correctamente");

            WebElement claveInput = driver.findElement(CLAVE_INPUT);
            claveInput.clear();
            claveInput.sendKeys(testData.credentials.emanager.password);
            System.out.println("Contraseña ingresada correctamente");

            WebElement botonIngresar = wait.until(ExpectedConditions.elementToBeClickable(BOTON_INGRESAR));
            botonIngresar.click();
            System.out.println("Clic en botón de ingreso realizado");

            // Esperar a que aparezca un elemento que confirme login exitoso
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(ELEMENT_AFTER_LOGIN));
                System.out.println("Login en eManager exitoso - Elemento post-login detectado");
            } catch (Exception e) {
                // Si no se encuentra ese elemento, intentemos verificar de otra manera
                // Por ejemplo, verificando la URL después del login o algún otro elemento
                System.out.println("Elemento estándar post-login no encontrado, verificando URL...");
                wait.until(ExpectedConditions.urlContains("App/Inicio.aspx"));
                System.out.println("Login en eManager exitoso - URL de inicio detectada");
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error en login de eManager: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isSessionActive(WebDriver driver) {
        try {
            // Verificar si existe el elemento que aparece después del login
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(ELEMENT_AFTER_LOGIN));
                System.out.println("Sesión de eManager activa detectada por elemento");
                return true;
            } catch (Exception e) {
                // Si no se encuentra ese elemento, intentemos verificar de otra manera
                if (driver.getCurrentUrl().contains("App/Inicio.aspx")) {
                    System.out.println("Sesión de eManager activa detectada por URL");
                    return true;
                }
            }

            System.out.println("No se detectó sesión activa de eManager");
            return false;
        } catch (Exception e) {
            System.out.println("Error al verificar sesión de eManager: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getStrategyName() {
        return "eManager Login";
    }
}