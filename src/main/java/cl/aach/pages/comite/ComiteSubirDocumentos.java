package cl.aach.pages.comite;

import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class ComiteSubirDocumentos {

    // ==============================
    // Localizadores (constantes)
    // ==============================
    private static final By CONSULTOR_BUTTON = By.xpath("//div[contains(@name, 'GJE') and normalize-space()='COORDINADOR COMITES']");
    private static final By DOCUMENTOS_BUTTON = By.id("DOCUMENTS206");
    private static final By AGREGAR_DOCUMENTO_BUTTON = By.id("body_btnAddDocument");
    private static final By COMITE_INPUT = By.name("ctl00$body$DCommittees");
    private static final By REUNION_INPUT = By.name("ctl00$body$DMeeting");
    private static final By TIPO_INPUT = By.name("ctl00$body$DType");
    private static final By AUTOR_INPUT = By.name("ctl00$body$DAuthor");
    private static final By CHECKBOX_NOTIFICAR = By.xpath("//*[@id=\"md-document\"]/div/div[2]/div[7]/div/label/span");
    private static final By UPLOAD_BUTTON = By.name("ctl00$body$DFiles");
    private static final By GUARDAR_BUTTON = By.id("body_btnSaveDocuments");
    private static final By RESULTADO_LABEL = By.xpath("//*[@id=\"body_GridViewDocuments\"]/tbody/tr[1]/td[4]");
    private static final By TABLA_DOCUMENTOS = By.xpath("//*[@id='body_GridViewDocuments']");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor que recibe un WebDriver existente
    // ==============================
    public ComiteSubirDocumentos(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Constructor sin parámetros que crea el WebDriver con un directorio único para el perfil
    // ==============================
    public ComiteSubirDocumentos() {
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
     * flujoAcciones hace clic en los enlaces y botones necesarios para llegar al formulario para subir archivos.
     */
    public void flujoAcciones() {
        wait.until(ExpectedConditions.elementToBeClickable(DOCUMENTOS_BUTTON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(AGREGAR_DOCUMENTO_BUTTON)).click();
    }

    /**
     * ingresarDatos es el encargado de enviar los datos a los localizadores correspondientes.
     */
    public void ingresarDatos(String comite,
                              String reunion,
                              String tipo,
                              String autor) {
        // 1.- Seleccionar el comité usando Select
        WebElement comiteDropdown = driver.findElement(COMITE_INPUT);
        Select selectComite = new Select(comiteDropdown);
        selectComite.selectByVisibleText(comite);

        // 2.- Seleccionar la reunión usando Select (buscando coincidencias parciales)
        WebElement reunionDropdown = driver.findElement(REUNION_INPUT);
        Select selectReunion = new Select(reunionDropdown);
        boolean isFound = false;
        for (WebElement option : selectReunion.getOptions()) {
            if (option.getText().contains(reunion)) {
                selectReunion.selectByVisibleText(option.getText());
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            throw new RuntimeException("No se encontró una opción que contenga el texto: " + reunion);
        }

        // 3.- Seleccionar un tipo
        WebElement tipoDropdown = driver.findElement(TIPO_INPUT);
        Select selectTipo = new Select(tipoDropdown);
        selectTipo.selectByVisibleText(tipo);

        // 4.- Seleccionar un autor
        WebElement autorDropdown = driver.findElement(AUTOR_INPUT);
        Select selectAutor = new Select(autorDropdown);
        selectAutor.selectByVisibleText(autor);

        // 5.- Subir Archivo
        WebElement uploadInput = driver.findElement(UPLOAD_BUTTON);
        try {
            // Obtener la URL del archivo desde los recursos
            URL resource = getClass().getClassLoader().getResource("documentoPruebaAutomatizada.pdf");
            if (resource == null) {
                throw new RuntimeException("El archivo 'documentoPruebaAutomatizada.pdf' no se encuentra en src/test/resources.");
            }
            // Convertir la URL en un archivo y obtener la ruta absoluta
            File file = new File(resource.toURI());
            String filePath = file.getAbsolutePath();
            // Pasar la ruta al input tipo file
            uploadInput.sendKeys(filePath);
            System.out.println("Archivo seleccionado correctamente: " + filePath);
        } catch (Exception e) {
            System.err.println("Error al seleccionar el archivo: " + e.getMessage());
        }
    }

    /**
     * clickGuardarDocumento hace clic en el botón Guardar Documento después de completar todos los campos.
     */
    public void clickGuardarDocumento() {
        wait.until(ExpectedConditions.elementToBeClickable(GUARDAR_BUTTON)).click();
    }

    /**
     * validarDocumentoTabla recorre la tabla de documentos buscando el documento de tipo Tabla.
     */
    public void validarDocumentoTabla() {
        // Se espera a que la tabla esté presente para obtener una referencia fresca
        WebElement tabla = wait.until(ExpectedConditions.presenceOfElementLocated(TABLA_DOCUMENTOS));
        List<WebElement> filas = tabla.findElements(By.xpath(".//tbody/tr"));
        boolean encontrado = false;
        for (WebElement fila : filas) {
            WebElement columnaArchivo = fila.findElement(By.xpath("./td[4]"));
            String textoArchivo = columnaArchivo.getText();
            if (textoArchivo.startsWith("TBL") && textoArchivo.endsWith("documentoPruebaAutomatizada.pdf")) {
                System.out.println("Documento encontrado Tabla: " + textoArchivo);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.err.println("No se encontró ningún documento de tipo Tabla.");
        }
    }

    /**
     * validarDocumentoMinuta recorre la tabla de documentos buscando el documento de tipo Minuta.
     */
    public void validarDocumentoMinuta() {
        WebElement tabla = wait.until(ExpectedConditions.presenceOfElementLocated(TABLA_DOCUMENTOS));
        List<WebElement> filas = tabla.findElements(By.xpath(".//tbody/tr"));
        boolean encontrado = false;
        for (WebElement fila : filas) {
            WebElement columnaArchivo = fila.findElement(By.xpath("./td[4]"));
            String textoArchivo = columnaArchivo.getText();
            if (textoArchivo.startsWith("MIN") && textoArchivo.endsWith("documentoPruebaAutomatizada.pdf")) {
                System.out.println("Documento encontrado Minuta: " + textoArchivo);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.err.println("No se encontró ningún documento de tipo Minuta.");
        }
    }

    /**
     * validarDocumentoDocumento recorre la tabla de documentos buscando el documento de tipo Documento.
     */
    public void validarDocumentoDocumento() {
        WebElement tabla = wait.until(ExpectedConditions.presenceOfElementLocated(TABLA_DOCUMENTOS));
        List<WebElement> filas = tabla.findElements(By.xpath(".//tbody/tr"));
        boolean encontrado = false;
        for (WebElement fila : filas) {
            WebElement columnaArchivo = fila.findElement(By.xpath("./td[4]"));
            String textoArchivo = columnaArchivo.getText();
            if (textoArchivo.startsWith("DOC") && textoArchivo.endsWith("documentoPruebaAutomatizada.pdf")) {
                System.out.println("Documento encontrado: " + textoArchivo);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.err.println("No se encontró ningún documento de tipo Documento.");
        }
    }

    /**
     * validarDocumentoAsistencia recorre la tabla de documentos buscando el documento de tipo Asistencia.
     */
    public void validarDocumentoAsistencia() {
        WebElement tabla = wait.until(ExpectedConditions.presenceOfElementLocated(TABLA_DOCUMENTOS));
        List<WebElement> filas = tabla.findElements(By.xpath(".//tbody/tr"));
        boolean encontrado = false;
        for (WebElement fila : filas) {
            WebElement columnaArchivo = fila.findElement(By.xpath("./td[4]"));
            String textoArchivo = columnaArchivo.getText();
            if (textoArchivo.startsWith("ASSI") && textoArchivo.endsWith("documentoPruebaAutomatizada.pdf")) {
                System.out.println("Documento Asistencia encontrado: " + textoArchivo);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.err.println("No se encontró ningún documento de tipo Asistencia.");
        }
    }
}
