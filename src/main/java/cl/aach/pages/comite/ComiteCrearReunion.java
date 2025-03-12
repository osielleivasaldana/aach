package cl.aach.pages.comite;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.FileUtils;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;

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
    // Constructor
    // ==============================
    public ComiteCrearReunion(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos públicos
    // ==============================

    /**
     * clickConsultor es el encargado de iniciar el sistema en el lanzador de aplicaciones.
     * Maneja la apertura de una nueva pestaña y las posibles alertas.
     */
    public void clickConsultor() {
        tabManager.saveCurrentTab(); // Guarda la pestaña actual antes de abrir una nueva
        System.out.println("Pestaña guardada antes de hacer clic en consultor");

        // Esperar a que el botón esté presente y sea visible
        WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(CONSULTOR_BUTTON));
        System.out.println("Botón 'consultor' localizado.");

        // Verificar el número inicial de pestañas
        int initialTabCount = driver.getWindowHandles().size();
        System.out.println("Número de pestañas antes del clic: " + initialTabCount);

        // Hacer clic en el botón
        try {
            boton.click();
            System.out.println("Se hizo clic en el botón 'consultor'.");
        } catch (Exception e) {
            // Si falla el clic normal, intentar con JavaScript
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", boton);
                System.out.println("Se hizo clic en el botón 'consultor' usando JavaScript.");
            } catch (Exception e2) {
                System.err.println("Error al hacer clic en el botón 'consultor': " + e2.getMessage());
                return;
            }
        }

        // Verificar si aparece una alerta
        try {
            // Esperar brevemente por una alerta
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            alertWait.until(ExpectedConditions.alertIsPresent());

            // Si hay una alerta, capturar su mensaje y aceptarla
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Alerta detectada: " + alertText);
            alert.accept();
            System.out.println("Alerta aceptada.");

            // No continuar con el cambio de pestaña
            return;
        } catch (Exception e) {
            // No hay alerta, continuamos normalmente
            System.out.println("No se detectó ninguna alerta.");
        }

        // Esperar un momento para que se abra la nueva pestaña
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verificar si se abrió una nueva pestaña
        int finalTabCount = driver.getWindowHandles().size();
        System.out.println("Número de pestañas después del clic: " + finalTabCount);

        if (finalTabCount > initialTabCount) {
            // Se abrió una nueva pestaña, cambiar a ella
            tabManager.switchToNewTab();
            System.out.println("Cambio a la nueva pestaña realizado.");

            // Esperar a que la nueva página cargue
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            System.err.println("ADVERTENCIA: No se abrió una nueva pestaña después del clic.");
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
