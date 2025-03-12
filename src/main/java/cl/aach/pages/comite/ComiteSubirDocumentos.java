package cl.aach.pages.comite;

import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;

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

    // XPath para la tabla de documentos (usado en las validaciones)
    private static final By TABLA_DOCUMENTOS = By.xpath("//*[@id='body_GridViewDocuments']");

    // ==============================
    // Campos de instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_WAIT_MS = 1000;

    // ==============================
    // Constructor
    // ==============================
    public ComiteSubirDocumentos(WebDriver driver) {
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
        // Agregamos una pequeña pausa para dar tiempo a que la tabla se actualice
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Método genérico para validar documentos de cualquier tipo
     * Maneja excepciones StaleElementReferenceException con reintentos
     *
     * @param tipoPrefix Prefijo del tipo de documento (TBL, MIN, DOC, ASSI)
     * @param tipoNombre Nombre descriptivo del tipo de documento
     */
    private void validarDocumento(String tipoPrefix, String tipoNombre) {
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                // Esperar a que la tabla esté presente y asegurarse de que está actualizada
                WebElement tabla = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(TABLA_DOCUMENTOS)));

                List<WebElement> filas = tabla.findElements(By.xpath(".//tbody/tr"));
                boolean encontrado = false;

                for (WebElement fila : filas) {
                    WebElement columnaArchivo = fila.findElement(By.xpath("./td[4]"));
                    String textoArchivo = columnaArchivo.getText();
                    if (textoArchivo.startsWith(tipoPrefix) &&
                            textoArchivo.endsWith("documentoPruebaAutomatizada.pdf")) {
                        System.out.println("Documento encontrado " + tipoNombre + ": " + textoArchivo);
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    System.err.println("No se encontró ningún documento de tipo " + tipoNombre + ".");
                }

                // Si llegamos aquí sin excepción, salimos del bucle de reintentos
                return;

            } catch (StaleElementReferenceException e) {
                retryCount++;
                System.out.println("Reintento " + retryCount + " para validar documento " + tipoNombre +
                        ": Elemento obsoleto detectado");

                try {
                    Thread.sleep(RETRY_WAIT_MS); // Espera breve antes de reintentar
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                // Si hemos agotado todos los reintentos, lanzamos la excepción
                if (retryCount >= MAX_RETRIES) {
                    throw new RuntimeException("No se pudo validar el documento después de " +
                            MAX_RETRIES + " intentos: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * validarDocumentoTabla recorre la tabla de documentos buscando el documento de tipo Tabla.
     */
    public void validarDocumentoTabla() {
        validarDocumento("TBL", "Tabla");
    }

    /**
     * validarDocumentoMinuta recorre la tabla de documentos buscando el documento de tipo Minuta.
     */
    public void validarDocumentoMinuta() {
        validarDocumento("MIN", "Minuta");
    }

    /**
     * validarDocumentoDocumento recorre la tabla de documentos buscando el documento de tipo Documento.
     */
    public void validarDocumentoDocumento() {
        validarDocumento("DOC", "Documento");
    }

    /**
     * validarDocumentoAsistencia recorre la tabla de documentos buscando el documento de tipo Asistencia.
     */
    public void validarDocumentoAsistencia() {
        validarDocumento("ASSI", "Asistencia");
    }
}