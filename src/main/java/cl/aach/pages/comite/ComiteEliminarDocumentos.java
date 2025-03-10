package cl.aach.pages.comite;

import cl.aach.utils.DocumentManager;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.UUID;

public class ComiteEliminarDocumentos {

    // ==============================
    // Localizadores (constantes)
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//div[contains(@name, 'GJE') and normalize-space()='COORDINADOR COMITES']");
    private static final By DOCUMENTOS_BUTTON = By.id("DOCUMENTS206");
    private static final By PROPIEDADES_DOCUMENTO = By.xpath("//*[@id=\"body_GridViewDocuments\"]/tbody/tr[1]/td[1]/div[1]");
    private static final By TABLA = By.xpath("//*[@id=\"body_GridViewDocuments\"]");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor que recibe un WebDriver existente
    // ==============================
    public ComiteEliminarDocumentos(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.tabManager = new TabManager(driver);
    }

    // ==============================
    // Constructor sin parámetros que crea el WebDriver con un directorio único para el perfil
    // ==============================
    public ComiteEliminarDocumentos() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-features=IsolateOrigins,site-per-process");

        // Agregar un directorio único para user-data-dir
        try {
            String uniqueProfile = Files.createTempDirectory("chrome_profile_" + UUID.randomUUID()).toString();
            options.addArguments("--user-data-dir=" + uniqueProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.tabManager = new TabManager(driver);
    }

    // ==============================
    // Métodos públicos
    // ==============================

    /**
     * clickConsultor es el encargado de iniciar el sistema en el lanzador de aplicaciones.
     */
    public void clickConsultor() {
        tabManager.saveCurrentTab(); // Guarda la pestaña actual antes de abrir una nueva
        WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
        boton.click();
        tabManager.switchToNewTab();
    }

    /**
     * flujoAcciones hace clic en los enlaces y botones necesarios para llegar al formulario para eliminar archivos.
     */
    public void flujoAcciones() {
        wait.until(ExpectedConditions.elementToBeClickable(DOCUMENTOS_BUTTON)).click();
    }

    /**
     * eliminarDocumento elimina todos los documentos relacionados con los test.
     */
    public void eliminarDocumento(String doc1) {
        DocumentManager documentManager = new DocumentManager(driver);
        documentManager.eliminarDocumentosTabla("documentoPruebaAutomatizada.pdf");
    }
}
