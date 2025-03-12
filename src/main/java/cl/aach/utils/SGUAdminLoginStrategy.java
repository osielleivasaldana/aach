package cl.aach.utils;

import cl.aach.models.TestData;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Implementación de la estrategia de login para el sistema SGU con credenciales de administrador.
 */
public class SGUAdminLoginStrategy implements LoginStrategy {

    private final TestData testData;

    // Localizadores para el login de SGU
    private static final By USUARIO_INPUT = By.id("loginUsuario");
    private static final By CLAVE_INPUT = By.id("loginContrasenya");
    private static final By BOTON_INGRESAR = By.xpath("//*[@id=\"tb\"]/tbody/tr[3]/td/center/div/div[2]/div[4]/button");
    private static final By ELEMENT_AFTER_LOGIN = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_AccAdministrador\"]/div/a[1]"); // Elemento presente después de login exitoso

    /**
     * Constructor que recibe los datos de prueba necesarios para el login.
     *
     * @param testData datos de prueba con credenciales
     */
    public SGUAdminLoginStrategy(TestData testData) {
        this.testData = testData;
    }

    @Override
    public String getBaseUrl() {
        return ConfigUtil.getProperty("sgu.url");
    }

    @Override
    public boolean performLogin(WebDriver driver) {
        try {
            // Verificar que estamos en la URL correcta primero
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("sgu.aachtest.cl")) {
                System.out.println("Navegando a URL de SGU Admin: " + getBaseUrl());
                driver.get(getBaseUrl());
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement usuarioInput = wait.until(ExpectedConditions.presenceOfElementLocated(USUARIO_INPUT));
            usuarioInput.clear(); // Limpiar el campo primero
            usuarioInput.sendKeys(testData.credentials.portalAdmin.user);

            WebElement claveInput = driver.findElement(CLAVE_INPUT);
            claveInput.clear(); // Limpiar el campo primero
            claveInput.sendKeys(testData.credentials.portalAdmin.password);

            WebElement botonIngresar = driver.findElement(BOTON_INGRESAR);
            botonIngresar.click();

            // Esperar a que aparezca un elemento que confirme login exitoso
            wait.until(ExpectedConditions.presenceOfElementLocated(ELEMENT_AFTER_LOGIN));

            System.out.println("Login exitoso en SGU Admin con usuario: " + testData.credentials.portalAdmin.user);
            return true;
        } catch (Exception e) {
            System.err.println("Error en login de SGU Admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isSessionActive(WebDriver driver) {
        try {
            // Verificar que estamos en la URL correcta
            String currentUrl = driver.getCurrentUrl();
            boolean inCorrectDomain = currentUrl.contains("sgu.aachtest.cl");

            if (!inCorrectDomain) {
                return false;
            }

            // Verificar si existe el elemento que aparece después del login
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.presenceOfElementLocated(ELEMENT_AFTER_LOGIN));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getStrategyName() {
        return "SGU Admin Login";
    }
}