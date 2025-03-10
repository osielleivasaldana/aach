package cl.aach.pages.comite;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.FileUtils;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class ComiteCrearReunion {

    // ==============================
    // Localizadores (constantes)
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//div[contains(@name, 'GJE') and normalize-space()='COORDINADOR COMITES']");
    private static final By REUNIONES_BUTTON = By.id("MEETINGS203");
    private static final By COORDINAR_REUNION = By.id("body_btnAddMeeting");
    private static final By COMITE_INPUT = By.xpath("//*[@id=\"body_CMCommittees\"]");
    private static final By ASUNTO_INPUT = By.name("ctl00$body$CMAffair");
    private static final By UBICACION_INPUT = By.name("ctl00$body$CMLocation");
    private static final By FECHA_INPUT = By.name("ctl00$body$CMDate");
    private static final By H_INICIO_INPUT = By.name("ctl00$body$CMTimeFrom");
    private static final By H_TERMINO_INPUT = By.name("ctl00$body$CMTimeTo");
    private static final By DESC_INPUT = By.name("ctl00$body$CMDescription");
    private static final By CHECKBOX_INVITAR = By.xpath("//tr[td[text()='OSIEL LEIVA'] and td[text()='ASOCIACIÓN DE ASEGURADORES DE CHILE A.G.']]//input[@type='checkbox']");
    private static final By BUSCAR_BUTTON = By.id("body_btnSaveMeetting");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"body_GridViewMeetings\"]/tbody/tr[1]/td[3]");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor que crea el WebDriver con un directorio único para el perfil
    // ==============================
    public ComiteCrearReunion() {
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

    // Constructor alternativo que recibe un WebDriver ya creado
    public ComiteCrearReunion(WebDriver driver) {
        this.driver = driver;
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
        try {
            // Guarda la pestaña actual antes de abrir una nueva
            tabManager.saveCurrentTab();

            // Esperar hasta que el botón esté presente
            WebElement boton = wait.until(ExpectedConditions.presenceOfElementLocated(CONSULTOR_BUTTON));
            System.out.println("Botón 'consultor' localizado.");

            // Hacer clic en el botón usando ClickUtils
            ClickUtils.click(driver, boton);
            System.out.println("Se hizo clic en el botón 'consultor'.");

            // Cambiar a la nueva pestaña
            tabManager.switchToNewTab();
            System.out.println("Cambio a la nueva pestaña exitoso.");
        } catch (Exception e) {
            throw new RuntimeException("Error en clickConsultor: No se pudo abrir el sistema en el lanzador de aplicaciones. Detalle: " + e.getMessage(), e);
        }
    }

    /**
     * flujoAcciones hace clic en los enlaces y botones necesarios para llegar al formulario de creación.
     */
    public void flujoAcciones() {
        wait.until(ExpectedConditions.elementToBeClickable(REUNIONES_BUTTON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(COORDINAR_REUNION)).click();
    }

    /**
     * ingresarDatos es el encargado de enviar los datos a los localizadores correspondientes.
     */
    public void ingresarDatos(String comite,
                              String asunto,
                              String ubicacion,
                              String fecha,
                              String horaInicio,
                              String horaTermino,
                              String desc) {
        // 1.- Seleccionar el comité usando Select
        WebElement comiteDropdown = driver.findElement(COMITE_INPUT);
        Select selectComite = new Select(comiteDropdown);
        selectComite.selectByVisibleText(comite);

        // 2.- Llenar el campo "asunto"
        driver.findElement(ASUNTO_INPUT).sendKeys(asunto);

        // 3.- Llenar el campo "ubicación"
        driver.findElement(UBICACION_INPUT).sendKeys(ubicacion);

        // 4.- Llenar el campo "fecha" usando JavaScript
        WebElement fechaElement = driver.findElement(FECHA_INPUT);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].value = arguments[1];", fechaElement, fecha);

        // 5.- Llena los campos hora inicio, hora termino y descripción
        driver.findElement(H_INICIO_INPUT).sendKeys(horaInicio);
        driver.findElement(H_TERMINO_INPUT).sendKeys(horaTermino);
        driver.findElement(DESC_INPUT).sendKeys(desc);

        // 6.- Hacer clic en el checkbox del usuario que será invitado
        WebElement checkbox = driver.findElement(CHECKBOX_INVITAR);
        ClickUtils.click(driver, checkbox);

        // 7.- Hacer clic en el botón Coordinar Reunión
        WebElement botonCoordinarReunion = driver.findElement(BUSCAR_BUTTON);
        ClickUtils.click(driver, botonCoordinarReunion);
    }

    /**
     * validarResultado es el encargado de validar que la reunión se creó correctamente.
     */
    public void validarResultado(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO_LABEL));
        String actualText = resultLabel.getText();
        assert actualText.equals(expectedText) :
                "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }

    /**
     * valdateFileDownload es el encargado de validar la descarga correcta del archivo ICS.
     */
    public void valdateFileDownload() {
        // Espera hasta que el archivo esté disponible o se agote el tiempo máximo (30 segundos)
        boolean isDownloaded = FileUtils.waitForFileDownload("ReunionComite.ics", 30);

        if (isDownloaded) {
            System.out.println("El archivo 'ReunionComite.ics' fue descargado correctamente.");
        } else {
            System.err.println("El archivo 'ReunionComite.ics' no se descargó.");
        }
        // Eliminamos el archivo descargado
        FileUtils.cleanDownloadedFile("ReunionComite.ics");
    }
}
