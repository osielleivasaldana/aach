package cl.aach.utils;

import cl.aach.models.TestData;
import cl.aach.pages.LoginPage;
import org.openqa.selenium.WebDriver;

/**
 * Implementación de la estrategia de login para el Portal con credenciales de administrador.
 * Esta estrategia se utiliza para acceder a la URL de usuarios.aachtest.cl con permisos administrativos.
 */
public class PortalAdminLoginStrategy implements LoginStrategy {

    private final TestData testData;

    /**
     * Constructor que recibe los datos de prueba necesarios para el login.
     *
     * @param testData datos de prueba con credenciales
     */
    public PortalAdminLoginStrategy(TestData testData) {
        this.testData = testData;
    }

    @Override
    public String getBaseUrl() {
        return ConfigUtil.getProperty("server.url"); // Esto apunta a usuarios.aachtest.cl/Login.aspx
    }

    @Override
    public boolean performLogin(WebDriver driver) {
        try {
            // Verificar que estamos en la URL correcta primero
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("usuarios.aachtest.cl")) {
                System.out.println("Navegando a URL de Portal Admin: " + getBaseUrl());
                driver.get(getBaseUrl());
            }

            LoginPage loginPage = new LoginPage(driver);
            loginPage.login(
                    testData.credentials.portalAdmin.user,
                    testData.credentials.portalAdmin.password
            );

            System.out.println("Login exitoso en Portal Admin con usuario: " + testData.credentials.portalAdmin.user);
            return true;
        } catch (Exception e) {
            System.err.println("Error en login de Portal Admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isSessionActive(WebDriver driver) {
        // Verificamos si estamos en la URL correcta y si hay cookies de sesión
        String currentUrl = driver.getCurrentUrl();
        boolean inCorrectDomain = currentUrl.contains("usuarios.aachtest.cl");
        boolean hasCookies = !driver.manage().getCookies().isEmpty();

        return inCorrectDomain && hasCookies;
    }

    @Override
    public String getStrategyName() {
        return "Portal Admin Login";
    }
}