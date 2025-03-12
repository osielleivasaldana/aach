package cl.aach.pages.sgu;

import cl.aach.utils.ClickUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AprobarSolicitud {

    // ==============================
    // Constantes
    // ==============================
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // ==============================
    // Localizadores
    // ==============================
    private static final By BOTON_GESTION_USUARIOS = By.xpath("//*[@id=\"ctl00_tdbarra2\"]/a");
    private static final By BOTON_SOLICITUDES = By.cssSelector("a[title='Solicitudes'][href='SolicitudesUsuarios.aspx']");
    private static final By DETALLE = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_GridView1\"]/tbody/tr[2]/td[1]/input");
    private static final By OPERACION = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_tdDetalle\"]/table/tbody/tr[2]/td/table/tbody/tr[2]/td[4]");
    private static final By ESTADO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_tdEncabezado\"]/table/tbody/tr[3]/td[6]");
    private static final By SI = By.xpath("//input[@type='radio' and @value='1' and contains(@onclick, 'OcultarDiv')]");
    private static final By PROCESAR = By.name("btnProcesar");
    private static final By RESULTADO = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_dvResultado\"]/div/span");

    // ==============================
    // Campos de Instancia
    // ==============================
    private WebDriver driver;
    private WebDriverWait wait;

    // ==============================
    // Constructor
    // ==============================
    public AprobarSolicitud(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    // ==============================
    // Métodos Principales
    // ==============================

    @Step("Navegar a la gestión de solicitudes")
    public void gestionDeSolicitudes() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_GESTION_USUARIOS)));
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(BOTON_SOLICITUDES)));
    }

    @Step("Abrir el detalle de la solicitud")
    private void abrirSolicitud() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(DETALLE)));
    }

    @Step("Verificar estado de la solicitud")
    private void verificarEstado() {
        WebElement estadoElement = wait.until(ExpectedConditions.presenceOfElementLocated(ESTADO));
        String estadoTexto = estadoElement.getText().trim();
        if (!"Abierta".equalsIgnoreCase(estadoTexto)) {
            throw new AssertionError("No hay solicitudes abiertas para aprobar. Estado actual: " + estadoTexto);
        }
    }

    @Step("Verificar tipo de operación")
    private void verificarOperacion() {
        WebElement operacionElement = wait.until(ExpectedConditions.presenceOfElementLocated(OPERACION));
        String operacionTexto = operacionElement.getText().trim();
        if ("Denegar".equalsIgnoreCase(operacionTexto)) {
            System.out.println("Se autorizó la solicitud de denegar el acceso a DRP Pruebas");
        } else if ("Activar".equalsIgnoreCase(operacionTexto)) {
            System.out.println("Se autorizó la solicitud de Activar el acceso a DRP Pruebas");
        }
    }

    @Step("Procesar autorización de la solicitud")
    private void procesarAutorizacion() {
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(SI)));
        ClickUtils.click(driver, wait.until(ExpectedConditions.presenceOfElementLocated(PROCESAR)));
    }

    @Step("Autorizar solicitud")
    public void autorizarSolicitud() {
        try {
            abrirSolicitud();
            verificarEstado();
            verificarOperacion();
            procesarAutorizacion();
        } catch (AssertionError e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Step("Validar resultado esperado: {expectedText}")
    public void validarResultado(String expectedText) {
        WebElement resultLabel = wait.until(ExpectedConditions.presenceOfElementLocated(RESULTADO));
        String actualText = resultLabel.getText();
        assert actualText.equals(expectedText) :
                "Texto esperado: '" + expectedText + "', pero fue: '" + actualText + "'";
    }
}