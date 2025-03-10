package cl.aach.utils;

import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

/**
 * Clase utilitaria para gestionar pestañas del navegador.
 */
public class TabManager {

    private final WebDriver driver;
    private String originalTab; // Almacena el identificador de la pestaña original

    /**
     * Constructor de TabManager.
     *
     * @param driver Instancia de WebDriver utilizada para la navegación.
     */
    public TabManager(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("El WebDriver no puede ser nulo.");
        }
        this.driver = driver;
    }

    /**
     * Guarda la pestaña actual como la pestaña original.
     */
    public void saveCurrentTab() {
        originalTab = driver.getWindowHandle();
        if (originalTab == null || originalTab.isEmpty()) {
            throw new IllegalStateException("No se pudo guardar la pestaña actual.");
        }
        System.out.println("Pestaña original guardada: " + originalTab);
    }

    /**
     * Cambia a una nueva pestaña que se abre después de la pestaña original.
     */
    public void switchToNewTab() {
        System.out.println("Esperando a que se abra una nueva pestaña...");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfWindowsToBe(2));

        Set<String> allTabs = driver.getWindowHandles();
        for (String tab : allTabs) {
            if (!tab.equals(originalTab)) {
                System.out.println("Cambiando a la nueva pestaña: " + tab);
                driver.switchTo().window(tab);
                return;
            }
        }
        throw new IllegalStateException("No se encontró una nueva pestaña para cambiar.");
    }

    /**
     * Cierra la pestaña actual y vuelve a la pestaña original, manejando posibles excepciones.
     */
    public void closeAndReturnToOriginalTab() {
        System.out.println("Cerrando pestaña actual...");
        driver.close(); // Cierra la pestaña actual

       /** if (originalTab == null || originalTab.isEmpty()) {
            System.err.println("Advertencia: La pestaña original no está definida o no es válida.");
            return; // Salir silenciosamente si no hay pestaña original definida
        } **/

        // Verificar si la pestaña original sigue abierta
        Set<String> openTabs = driver.getWindowHandles();
        if (!openTabs.contains(originalTab)) {
            System.out.println("La pestaña original ya no está abierta.");
            return; // Salir silenciosamente si la pestaña original no está abierta
        }

        try {
            System.out.println("Volviendo a la pestaña original: " + originalTab);
            driver.switchTo().window(originalTab); // Cambiar a la pestaña original
        } catch (NoSuchWindowException e) {
            System.err.println("Error: No se pudo cambiar a la pestaña original: " + originalTab);
            throw new IllegalStateException("No se pudo cambiar a la pestaña original: " + originalTab, e);
        }
    }

    /**
     * Verifica si la pestaña actual es la pestaña original.
     *
     * @return `true` si la pestaña actual es la pestaña original, de lo contrario `false`.
     */
    public boolean isOnOriginalTab() {
        try {
            String currentTab = driver.getWindowHandle();
            return currentTab.equals(originalTab);
        } catch (NoSuchWindowException e) {
            System.err.println("Error al verificar la pestaña actual: " + e.getMessage());
            return false;
        }
    }
}
