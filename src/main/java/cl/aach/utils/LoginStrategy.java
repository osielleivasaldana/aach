package cl.aach.utils;

import org.openqa.selenium.WebDriver;

/**
 * Interfaz que define la estrategia de inicio de sesión.
 * Cada implementación representará una forma diferente de autenticación
 * según el sistema o aplicación que se esté probando.
 */
public interface LoginStrategy {

    /**
     * Obtiene la URL base del sistema que se probará.
     *
     * @return String con la URL base
     */
    String getBaseUrl();

    /**
     * Realiza el proceso de inicio de sesión en el sistema.
     *
     * @param driver el WebDriver que se usará para interactuar con la página
     * @return true si el login fue exitoso, false en caso contrario
     */
    boolean performLogin(WebDriver driver);

    /**
     * Verifica si la sesión fue iniciada correctamente.
     *
     * @param driver el WebDriver que se usará para verificar la sesión
     * @return true si la sesión está activa, false en caso contrario
     */
    boolean isSessionActive(WebDriver driver);

    /**
     * Nombre descriptivo de la estrategia de login para logging y reportes.
     *
     * @return nombre de la estrategia
     */
    String getStrategyName();
}