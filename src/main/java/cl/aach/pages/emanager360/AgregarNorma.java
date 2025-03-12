package cl.aach.pages.emanager360;

import cl.aach.utils.ConfigUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.time.Duration;

/**
 * Clase para agregar una nueva normativa en el sistema eManager 360.
 * Versión adaptada para trabajar con WebDriver proporcionado por BaseTest.
 */
public class AgregarNorma {

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_EMANAGER = ConfigUtil.getProperty("emanager.url");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final String FILE_NAME = "imagen-test.jpg"; // Archivo de prueba

    // ==============================
    // Localizadores
    // ==============================
    // Login y navegación
    private static final By USUARIO_INPUT = By.name("txUsuario");
    private static final By CLAVE_INPUT = By.name("txPassword");
    private static final By BOTON_INGRESAR = By.name("btnLogin");
    private static final By BOTON_CONTENIDO = By.id("bot05");
    private static final By BOTON_ARCHIVO = By.xpath("//*[@id=\"menu5\"]/table/tbody/tr[2]/td/a");
    private static final By BOTON_AGREGAR = By.id("BtnEnviar");
    private static final By VALIDAR_RESULTADOS = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_Alerta\"]");

    // Localizadores para la nueva ventana (formulario)
    private static final By CAMPO_NOMBRE = By.name("ctl00$ContentPlaceHolder1$Nombre");
    private static final By FECHA_VENCIMIENTO_AACH = By.name("ctl00$ContentPlaceHolder1$FechaVctoAACH");
    private static final By FECHA_VENCIMIENTO_MANDANTE = By.name("ctl00$ContentPlaceHolder1$FechaVctoMandante");
    private static final By CAMPO_MATERIA = By.name("ctl00$ContentPlaceHolder1$Materia");
    private static final By SUBIR_ARCHIVO = By.name("ctl00$ContentPlaceHolder1$FileUpload1");
    private static final By BOTON_AGREGAR_BORRADOR = By.name("ctl00$ContentPlaceHolder1$Button1");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private boolean driverCreatedInternally;

    // ==============================
    // Constructores
    // ==============================

    /**
     * Constructor que recibe un WebDriver ya configurado.
     * Este es el constructor que se usará con la nueva arquitectura de BaseTest.
     *
     * @param driver WebDriver previamente configurado e inicializado.
     */
    public AgregarNorma(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        this.driverCreatedInternally = false;
    }

    // ==============================
    // Métodos Principales
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
        WebElement contenidoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_CONTENIDO));
        new Actions(driver).moveToElement(contenidoElement).perform();
        WebElement archivoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_ARCHIVO));
        archivoElement.click();
    }

    /**
     * Hace clic en el botón Agregar.
     */
    public void clickAgregar() {
        WebElement botonAgregar = wait.until(ExpectedConditions.visibilityOfElementLocated(BOTON_AGREGAR));
        botonAgregar.click();
    }

    /**
     * Agrega un borrador de normativa completando el formulario y subiendo el archivo de prueba.
     *
     * @param nombre        Nombre de la normativa.
     * @param fechaAach     Fecha de vencimiento AACH.
     * @param fechaMandante Fecha de vencimiento Mandante.
     * @param materia       Materia.
     */
    public void agregarBorrador(String nombre, String fechaAach, String fechaMandante, String materia) {
        ingresarDatosNormativa(nombre, fechaAach, fechaMandante, materia);
        subirArchivoDePrueba();
        clickAgregarBorrador();
    }

    /**
     * Ingresa los datos de la normativa en el formulario.
     *
     * @param nombre        Nombre de la normativa.
     * @param fechaAach     Fecha de vencimiento AACH.
     * @param fechaMandante Fecha de vencimiento Mandante.
     * @param materia       Materia.
     */
    private void ingresarDatosNormativa(String nombre, String fechaAach, String fechaMandante, String materia) {
        wait.until(ExpectedConditions.presenceOfElementLocated(CAMPO_NOMBRE)).sendKeys(nombre);
        wait.until(ExpectedConditions.presenceOfElementLocated(FECHA_VENCIMIENTO_AACH)).sendKeys(fechaAach);
        wait.until(ExpectedConditions.presenceOfElementLocated(FECHA_VENCIMIENTO_MANDANTE)).sendKeys(fechaMandante);
        wait.until(ExpectedConditions.presenceOfElementLocated(CAMPO_MATERIA)).sendKeys(materia);
    }

    /**
     * Sube el archivo de prueba al formulario.
     */
    private void subirArchivoDePrueba() {
        WebElement uploadInput = driver.findElement(SUBIR_ARCHIVO);
        try {
            URL resource = getClass().getClassLoader().getResource(FILE_NAME);
            if (resource == null) {
                throw new RuntimeException("El archivo '" + FILE_NAME + "' no se encuentra en src/test/resources.");
            }
            File file = new File(resource.toURI());
            String filePath = file.getAbsolutePath();
            uploadInput.sendKeys(filePath);
            System.out.println("Archivo seleccionado correctamente: " + filePath);
        } catch (Exception e) {
            System.err.println("Error al seleccionar el archivo: " + e.getMessage());
        }
    }

    /**
     * Hace clic en el botón para agregar el borrador.
     */
    private void clickAgregarBorrador() {
        wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_AGREGAR_BORRADOR)).click();
    }

    /**
     * Valida que el resultado mostrado sea el esperado.
     *
     * @param expectedText Texto esperado en el mensaje de resultado.
     */
    public void validarResultado(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(VALIDAR_RESULTADOS));
        String actualText = resultLabel.getText();
        assert actualText.equals(expectedText) :
                "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }

    /**
     * Cierra el navegador.
     * Este método solo cerrará el navegador si fue creado internamente por esta clase.
     */
    public void close() {
        if (driver != null && driverCreatedInternally) {
            driver.quit();
        }
    }
}