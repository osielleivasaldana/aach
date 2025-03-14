package cl.aach.pages.sgm;

import cl.aach.utils.ClickUtils;
import cl.aach.utils.TabManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.time.Duration;

public class NuevoEnvio {

    // ==============================
    // Constantes y Localizadores
    // ==============================
    private static final By OPERADOR_DE_ENVIO = By.xpath("//*[@id=\"tbAccesosSistemas\"]/tbody/tr[24]/td[2]/div[2]");
    private static final By BOTON_MODIFICAR = By.xpath("//*[@id=\"tbBarra\"]/tbody/tr/td[2]");
    private static final By BOTON_ENVIAR = By.xpath("//*[@id=\"dvModulos\"]/table/tbody/tr[1]/td[2]/a");
    private static final By BOTON_DESDE_EXCEL = By.xpath("//*[@id=\"Modulo24\"]/table/tbody/tr[1]/td[3]/a");
    private static final By CARPETA = By.name("ctl00$ContentPlaceHolder1$Carpeta");
    private static final By LIBRO = By.name("ctl00$ContentPlaceHolder1$Libro");
    private static final By REFERENCIA = By.name("ctl00$ContentPlaceHolder1$Referencia");
    private static final By ARCHIVO_PDF = By.name("ctl00$ContentPlaceHolder1$arcPDF");
    private static final By ARCHIVO_EXCEL = By.name("ctl00$ContentPlaceHolder1$arcXLS");
    private static final By NO_REQUIERE_FIRMA = By.id("ctl00_ContentPlaceHolder1_firmaN");
    private static final By RESPONSABLE_TECNICO = By.name("ctl00$ContentPlaceHolder1$cbxRespTecnico");
    private static final By RESPONSABLE_TECNICO_FINAL = By.name("ctl00$ContentPlaceHolder1$cbxRespTecnicoFinal");
    // Nota: El localizador MODO_ENTREGA parece incorrecto; se deja tal como en el original
    private static final By MODO_ENTREGA = By.xpath("ctl00$ContentPlaceHolder1$ModoEntrega");
    private static final By GENERAR_ENVIO = By.name("ctl00$ContentPlaceHolder1$Button1");
    private static final By RESULTADO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_tdAlerta\"]/p[1]");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;
    private TabManager tabManager;

    // ==============================
    // Constructor
    // ==============================
    public NuevoEnvio(WebDriver driver) {
        this.driver = driver;
        this.tabManager = new TabManager(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ==============================
    // Métodos Principales
    // ==============================

    /**
     * clickConsultor guarda la pestaña actual y abre el sistema en una nueva pestaña.
     */
    public void clickConsultor() {
        tabManager.saveCurrentTab();
        WebElement consultor = wait.until(ExpectedConditions.presenceOfElementLocated(OPERADOR_DE_ENVIO));
        consultor.click();
        tabManager.switchToNewTab();
    }

    /**
     * subMenu hace clic en los botones de modificar, enviar y seleccionar "Desde Excel".
     */
    public void subMenu() {
        WebElement modificar = wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_MODIFICAR));
        ClickUtils.click(driver, modificar);

        WebElement envios = wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_ENVIAR));
        ClickUtils.click(driver, envios);

        WebElement excel = wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_DESDE_EXCEL));
        ClickUtils.click(driver, excel);
    }

    /**
     * realizarEnvio completa el formulario para realizar un envío.
     *
     * @param carpeta                  Carpeta a seleccionar.
     * @param libro                    Libro a seleccionar.
     * @param referencia               Referencia a ingresar.
     * @param responsableTecnico       Responsable técnico.
     * @param responsableTecnicoFinal  Responsable técnico final.
     */
    public void realizarEnvio(String carpeta, String libro, String referencia, String responsableTecnico, String responsableTecnicoFinal) {
        // 1. Seleccionar la CARPETA
        WebElement carpetaDropdown = driver.findElement(CARPETA);
        new Select(carpetaDropdown).selectByVisibleText(carpeta);

        // 2. Seleccionar el Libro
        WebElement libroDropdown = driver.findElement(LIBRO);
        new Select(libroDropdown).selectByVisibleText(libro);

        // 3. Ingresar la referencia
        driver.findElement(REFERENCIA).sendKeys(referencia);

        // 4. Subir Archivo PDF
        WebElement uploadPDF = driver.findElement(ARCHIVO_PDF);
        try {
            URL resource = getClass().getClassLoader().getResource("documentoPruebaAutomatizada.pdf");
            if (resource == null) {
                throw new RuntimeException("El archivo 'documentoPruebaAutomatizada.pdf' no se encuentra en src/test/resources.");
            }
            File file = new File(resource.toURI());
            String filePath = file.getAbsolutePath();
            uploadPDF.sendKeys(filePath);
            System.out.println("Archivo PDF seleccionado correctamente: " + filePath);
        } catch (Exception e) {
            System.err.println("Error al seleccionar el archivo PDF: " + e.getMessage());
        }

        // 5. Subir Archivo EXCEL
        WebElement uploadEXCEL = driver.findElement(ARCHIVO_EXCEL);
        try {
            URL resource = getClass().getClassLoader().getResource("MATRIZ.xlsx");
            if (resource == null) {
                throw new RuntimeException("El archivo 'MATRIZ.xlsx' no se encuentra en src/test/resources.");
            }
            File file = new File(resource.toURI());
            String filePath = file.getAbsolutePath();
            uploadEXCEL.sendKeys(filePath);
            System.out.println("Archivo EXCEL seleccionado correctamente: " + filePath);
        } catch (Exception e) {
            System.err.println("Error al seleccionar el archivo EXCEL: " + e.getMessage());
        }

        // 6. Hacer clic en "No requiere firma"
        WebElement firma = wait.until(ExpectedConditions.presenceOfElementLocated(NO_REQUIERE_FIRMA));
        ClickUtils.click(driver, firma);

        // 7. Seleccionar Responsable Técnico
        WebElement responsableDropdown = driver.findElement(RESPONSABLE_TECNICO);
        new Select(responsableDropdown).selectByVisibleText(responsableTecnico);

        // 8. Seleccionar Responsable Técnico Final
        WebElement responsableTecnicoDropdown = driver.findElement(RESPONSABLE_TECNICO_FINAL);
        new Select(responsableTecnicoDropdown).selectByVisibleText(responsableTecnicoFinal);

        // 9. Hacer clic en el botón "Generar Envío"
        WebElement generarEnvio = wait.until(ExpectedConditions.presenceOfElementLocated(GENERAR_ENVIO));
        ClickUtils.click(driver, generarEnvio);
    }

    /**
     * validarResultado compara el resultado mostrado con el texto esperado.
     *
     * @param expectedText Texto esperado.
     */
    public void validarResultado(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO));
        String actualText = resultLabel.getText();
        assert actualText.equals(expectedText) :
                "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }
}
