package cl.aach.pages.emanager;

import cl.aach.utils.ConfigUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SubirArchivo {

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_EMANAGER = ConfigUtil.getProperty("emanager.url");

    // Localizadores para login y navegación
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By BOTON_CONTENIDO = By.id("bot02");
    private static final By BOTON_ARCHIVO = By.xpath("//*[@id=\"menu2\"]/table/tbody/tr[4]/td/a");
    private static final By AGREGAR = By.id("btnAgregar2");
    private static final By BOTON_GUARDAR = By.id("ctl00_ContentPlaceHolder1_btnGuardar");

    // Localizadores para la nueva ventana (formulario)
    private static final By CAMPO_TITULO = By.name("ctl00$ContentPlaceHolder1$Titulo");
    private static final By CAMPO_DESCRIPCION = By.name("ctl00$ContentPlaceHolder1$Descripcion");
    private static final By CAMPO_MATERIA = By.name("ctl00$ContentPlaceHolder1$Materia");
    private static final By FECHA_PUBLICACION = By.name("ctl00$ContentPlaceHolder1$FechaPub");
    private static final By CAMPO_PRENSA = By.name("ctl00$ContentPlaceHolder1$DestacadoPrensa");
    private static final By SUBIR_ARCHIVO = By.name("ctl00$ContentPlaceHolder1$FileUpload1");
    private static final By PRIVADO = By.name("ctl00$ContentPlaceHolder1$Privado");
    private static final By FECHA_TERMINO = By.name("ctl00$ContentPlaceHolder1$FechaTermino");
    private static final By BOTON_AGREGAR_EN_FORMULARIO = By.name("ctl00$ContentPlaceHolder1$Button1");

    // ==============================
    // Constructor
    // ==============================
    public SubirArchivo() {
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

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        // Agregar un directorio único para user-data-dir
        try {
            String uniqueProfile = Files.createTempDirectory("chrome_profile_" + UUID.randomUUID()).toString();
            options.addArguments("--user-data-dir=" + uniqueProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * Abre la URL del servicio eManager.
     */
    public void abrirUrl() {
        driver.get(URL_EMANAGER);
    }

    /**
     * Realiza el login en el servicio.
     *
     * @param usuario Usuario a ingresar.
     * @param clave   Contraseña a ingresar.
     */
    public void login(String usuario, String clave) {
        wait.until(ExpectedConditions.presenceOfElementLocated(USUARIO_INPUT)).sendKeys(usuario);
        driver.findElement(CLAVE_INPUT).sendKeys(clave);
        driver.findElement(BOTON_INGRESAR).click();
    }

    /**
     * Navega al menú de archivos.
     */
    public void clickMenu() {
        WebElement consultasElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_CONTENIDO));
        Actions actions = new Actions(driver);
        actions.moveToElement(consultasElement).perform();
        WebElement archivosElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_ARCHIVO));
        archivosElement.click();
    }

    /**
     * Hace clic en el botón "Agregar".
     */
    public void clickAgregar() {
        WebElement botonAgregar = wait.until(ExpectedConditions.visibilityOfElementLocated(AGREGAR));
        botonAgregar.click();
    }

    /**
     * Completa el formulario para subir un archivo en la nueva ventana.
     *
     * @param titulo      Título del archivo.
     * @param descripcion Descripción del archivo.
     * @param materia     Materia a seleccionar.
     * @param prensa      Tipo de prensa a seleccionar.
     * @param archivo     Nombre del archivo a subir (ubicado en src/test/resources).
     * @param fecha       Fecha de término a ingresar.
     */
    public void completarFormularioSubirarchivo(String titulo, String descripcion, String materia, String prensa, String archivo, String fecha) {
        // Guardar la ventana original
        String ventanaOriginal = driver.getWindowHandle();

        // Esperar hasta que se abra una nueva ventana
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // Cambiar a la nueva ventana
        Set<String> ventanas = driver.getWindowHandles();
        for (String ventana : ventanas) {
            if (!ventana.equals(ventanaOriginal)) {
                driver.switchTo().window(ventana);
                break;
            }
        }

        // 1.- Ingresar el título
        wait.until(ExpectedConditions.presenceOfElementLocated(CAMPO_TITULO)).sendKeys(titulo);

        // 2.- Ingresar la descripción
        wait.until(ExpectedConditions.presenceOfElementLocated(CAMPO_DESCRIPCION)).sendKeys(descripcion);

        // 3.- Seleccionar una materia
        WebElement materiaDropdown = driver.findElement(CAMPO_MATERIA);
        Select selectMateria = new Select(materiaDropdown);
        selectMateria.selectByVisibleText(materia);

        // 4.- Seleccionar prensa
        WebElement prensaDropdown = driver.findElement(CAMPO_PRENSA);
        Select selectPrensa = new Select(prensaDropdown);
        selectPrensa.selectByVisibleText(prensa);

        // 5.- Subir Archivo
        WebElement uploadInput = driver.findElement(SUBIR_ARCHIVO);
        try {
            // Obtener la URL del archivo desde los recursos
            URL resource = getClass().getClassLoader().getResource(archivo);
            if (resource == null) {
                throw new RuntimeException("El archivo " + archivo + " no se encuentra en src/test/resources.");
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

        // 6.- Llenar el campo "fecha termino"
        wait.until(ExpectedConditions.presenceOfElementLocated(FECHA_TERMINO)).sendKeys(fecha + Keys.RETURN);

        // Volver a la ventana original
        driver.switchTo().window(ventanaOriginal);
    }

    /**
     * Cierra el WebDriver.
     */
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
