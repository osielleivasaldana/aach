package cl.aach.utils;

import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Utilidad para gestionar pestañas de navegador durante las pruebas.
 */
public class TabManager {
    private WebDriver driver;
    private Stack<String> tabHistory;
    private String originalTab;

    /**
     * Constructor que inicializa el gestor de pestañas con el WebDriver.
     *
     * @param driver WebDriver a utilizar
     */
    public TabManager(WebDriver driver) {
        this.driver = driver;
        this.tabHistory = new Stack<>();

        try {
            this.originalTab = driver.getWindowHandle();
            System.out.println("Pestaña principal registrada: " + this.originalTab);
        } catch (Exception e) {
            System.err.println("Error al obtener pestaña original: " + e.getMessage());
            this.originalTab = null;
        }
    }

    /**
     * Guarda la pestaña actual para poder volver a ella después.
     */
    public void saveCurrentTab() {
        try {
            String currentTab = driver.getWindowHandle();
            tabHistory.push(currentTab);
            System.out.println("Pestaña guardada: " + currentTab);
        } catch (Exception e) {
            System.err.println("Error al guardar pestaña actual: " + e.getMessage());
        }
    }

    /**
     * Cambia a una nueva pestaña que se haya abierto recientemente.
     * Espera hasta que haya una nueva pestaña disponible.
     */
    public void switchToNewTab() {
        try {
            // Guardamos la pestaña actual como referencia
            String currentTab = driver.getWindowHandle();
            System.out.println("Pestaña actual antes de cambiar: " + currentTab);

            // Esperamos brevemente para que se abra la nueva pestaña
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Obtenemos todas las pestañas disponibles
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            System.out.println("Número de pestañas disponibles: " + tabs.size());

            if (tabs.size() <= 1) {
                // Si solo hay una pestaña, esperamos un poco más
                System.out.println("Esperando a que se abra una nueva pestaña...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                tabs = new ArrayList<>(driver.getWindowHandles());
                System.out.println("Número de pestañas después de espera: " + tabs.size());
            }

            if (tabs.size() <= 1) {
                System.err.println("ERROR: No se abrió ninguna pestaña nueva después de esperar");
                return;  // No cambiamos de pestaña si no hay nuevas
            }

            // Buscamos una pestaña diferente a la actual
            String newTab = null;
            for (String tab : tabs) {
                if (!tab.equals(currentTab)) {
                    newTab = tab;
                    break;
                }
            }

            if (newTab != null) {
                driver.switchTo().window(newTab);
                System.out.println("Cambiado a nueva pestaña: " + newTab);
            } else {
                System.err.println("No se encontró una pestaña diferente a la actual");
            }
        } catch (Exception e) {
            System.err.println("Error al cambiar a nueva pestaña: " + e.getMessage());
        }
    }

    /**
     * Cierra la pestaña actual y vuelve a la original.
     */
    public void closeAndReturnToOriginalTab() {
        try {
            // Obtenemos las pestañas antes de cerrar
            ArrayList<String> tabsBefore = new ArrayList<>(driver.getWindowHandles());
            System.out.println("Pestañas antes de cerrar: " + tabsBefore.size());

            // Cierra la pestaña actual
            String currentTab = driver.getWindowHandle();
            System.out.println("Cerrando pestaña: " + currentTab);
            driver.close();

            // Esperamos brevemente para que se cierre completamente
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Obtenemos las pestañas restantes
            ArrayList<String> tabsAfter = new ArrayList<>(driver.getWindowHandles());
            System.out.println("Pestañas después de cerrar: " + tabsAfter.size());

            // Si hay pestañas disponibles, volvemos a la original o a la primera disponible
            if (!tabsAfter.isEmpty()) {
                String targetTab = null;

                // Intentamos usar el historial si no está vacío
                if (!tabHistory.isEmpty()) {
                    String previousTab = tabHistory.pop();
                    if (tabsAfter.contains(previousTab)) {
                        targetTab = previousTab;
                        System.out.println("Volviendo a pestaña anterior del historial: " + targetTab);
                    }
                }

                // Si no pudimos usar el historial, intentamos con la original
                if (targetTab == null && originalTab != null && tabsAfter.contains(originalTab)) {
                    targetTab = originalTab;
                    System.out.println("Volviendo a pestaña original: " + targetTab);
                }

                // Si no pudimos usar ni el historial ni la original, usamos la primera disponible
                if (targetTab == null) {
                    targetTab = tabsAfter.get(0);
                    System.out.println("Volviendo a primera pestaña disponible: " + targetTab);
                }

                // Cambiamos a la pestaña objetivo
                driver.switchTo().window(targetTab);
            } else {
                System.err.println("ERROR: No hay pestañas disponibles después de cerrar la actual");
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar y volver a pestaña original: " + e.getMessage());

            // Intento de recuperación: cambiar a cualquier pestaña disponible
            try {
                ArrayList<String> availableTabs = new ArrayList<>(driver.getWindowHandles());
                if (!availableTabs.isEmpty()) {
                    driver.switchTo().window(availableTabs.get(0));
                    System.out.println("Recuperación: cambiado a la primera pestaña disponible");
                }
            } catch (Exception e2) {
                System.err.println("Error en recuperación: " + e2.getMessage());
            }
        }
    }

    /**
     * Cierra todas las pestañas excepto la original o la primera disponible.
     */
    public void closeAllTabs() {
        try {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());

            // Si no hay pestañas o solo hay una, no hay nada que hacer
            if (tabs.size() <= 1) {
                return;
            }

            // Determinar qué pestaña conservar (la original o la primera)
            String tabToKeep = (originalTab != null && tabs.contains(originalTab)) ?
                    originalTab : tabs.get(0);

            // Cerrar todas las demás pestañas
            for (String tab : tabs) {
                if (!tab.equals(tabToKeep)) {
                    try {
                        driver.switchTo().window(tab);
                        driver.close();
                        System.out.println("Pestaña cerrada: " + tab);
                    } catch (Exception e) {
                        System.err.println("Error al cerrar pestaña " + tab + ": " + e.getMessage());
                    }
                }
            }

            // Volver a la pestaña que conservamos
            driver.switchTo().window(tabToKeep);
            System.out.println("Volviendo a la pestaña conservada: " + tabToKeep);

            // Limpiar historial
            tabHistory.clear();
        } catch (Exception e) {
            System.err.println("Error al cerrar todas las pestañas: " + e.getMessage());
        }
    }

    /**
     * Cierra pestañas huérfanas (todas excepto la actual y la original).
     */
    public void closeOrphanedTabs() {
        try {
            String currentTab = driver.getWindowHandle();
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());

            // Si hay una o ninguna pestaña, no hay nada que hacer
            if (tabs.size() <= 1) {
                return;
            }

            // Creamos una lista de pestañas a conservar
            List<String> tabsToKeep = new ArrayList<>();

            // Siempre conservamos la pestaña actual
            tabsToKeep.add(currentTab);

            // También conservamos la pestaña original si existe
            if (originalTab != null && tabs.contains(originalTab) && !originalTab.equals(currentTab)) {
                tabsToKeep.add(originalTab);
            }

            // Cerramos todas las demás pestañas
            for (String tab : tabs) {
                if (!tabsToKeep.contains(tab)) {
                    try {
                        driver.switchTo().window(tab);
                        driver.close();
                        System.out.println("Pestaña huérfana cerrada: " + tab);
                    } catch (Exception e) {
                        System.err.println("Error al cerrar pestaña huérfana " + tab + ": " + e.getMessage());
                    }
                }
            }

            // Volvemos a la pestaña actual
            driver.switchTo().window(currentTab);

            // Limpiamos el historial de pestañas ya que puede contener pestañas cerradas
            tabHistory.clear();
        } catch (Exception e) {
            System.err.println("Error al cerrar pestañas huérfanas: " + e.getMessage());
        }
    }
}