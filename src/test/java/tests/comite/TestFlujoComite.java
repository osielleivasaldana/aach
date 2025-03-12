package tests.comite;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.comite.ComiteCrearReunion;
import cl.aach.pages.comite.ComiteSubirDocumentos;
import cl.aach.pages.comite.ComiteEliminarDocumentos;
import cl.aach.utils.DateUtils;
import cl.aach.utils.LoginStrategyFactory;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

@Epic("Comité")
@Feature("Gestión de reuniones y documentos en Comité")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.PORTAL) // Especificar estrategia de login explícitamente
public class TestFlujoComite extends BaseTest {

    private static TestFlujoComite currentTestInstance;

    @BeforeAll
    public static void iniciarFlujo() {
        // Activamos el flujo continuo para mantener el WebDriver entre tests
        startContinuousFlow();
    }

    @BeforeEach
    public void saveInstance() {
        // Guardar la instancia actual y asegurar el login
        currentTestInstance = this;
        performLogin(); // Realizamos el login explícitamente
    }

    public void run() {
        TC21092_stepCrearReunion();
        TC21121_stepSubirTabla();
        TC21171_stepSubirMinuta();
        TC21172_stepSubirDocumento();
        TC21173_stepSubirAsistencia();
        TC21174_stepEliminarDocumentos();
    }

    @Test
    @Order(1)
    @Description("Crear una nueva reunión con los datos configurados y la fecha dinámica del día siguiente.")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("tests.comite.TestFlujoComite.TC21092_stepCrearReunion")
    public void TC21092_stepCrearReunion() {
        ComiteCrearReunion crearReunion = new ComiteCrearReunion(driver);

        guardarPestana();

        hacerClickEnConsultor(crearReunion);

        accederFormularioReunion(crearReunion);

        ingresarDatosReunion(crearReunion);

        validarCreacionReunion(crearReunion);

        cerrarPestana();
    }

    @Test
    @Order(2)
    @Description("Subir un documento de tipo 'Tabla' al sistema Comité.")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("tests.comite.TestFlujoComite.TC21121_stepSubirTabla")
    public void TC21121_stepSubirTabla() {
        subirDocumento(testData.reunion.documentos.tipo.doc1, "Tabla");
    }

    @Test
    @Order(3)
    @Description("Subir un documento de tipo 'Minuta' al sistema Comité.")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("tests.comite.TestFlujoComite.TC21171_stepSubirMinuta")
    public void TC21171_stepSubirMinuta() {
        subirDocumento(testData.reunion.documentos.tipo.doc2, "Minuta");
    }

    @Test
    @Order(4)
    @Description("Subir un documento de tipo 'Documento' al sistema Comité.")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("tests.comite.TestFlujoComite.TC21172_stepSubirDocumento")
    public void TC21172_stepSubirDocumento() {
        subirDocumento(testData.reunion.documentos.tipo.doc3, "Documento");
    }

    @Test
    @Order(5)
    @Description("Subir un documento de tipo 'Asistencia' al sistema Comité.")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("tests.comite.TestFlujoComite.TC21173_stepSubirAsistencia")
    public void TC21173_stepSubirAsistencia() {
        subirDocumento(testData.reunion.documentos.tipo.doc4, "Asistencia");
    }

    @Test
    @Order(6)
    @Description("Eliminar documentos relacionados con el último test ejecutado en el sistema Comité.")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("tests.comite.TestFlujoComite.TC21174_stepEliminarDocumentos")
    public void TC21174_stepEliminarDocumentos() {
        ComiteEliminarDocumentos eliminarDocumentos = new ComiteEliminarDocumentos(driver);

        guardarPestana();

        hacerClickEnConsultor(eliminarDocumentos);

        accederListadoDocumentos(eliminarDocumentos);

        eliminarDocumento(eliminarDocumentos);

        cerrarPestana();
    }

    // Métodos de pasos reutilizables

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(ComiteCrearReunion consulta) {
        consulta.clickConsultor();
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(ComiteSubirDocumentos consulta) {
        consulta.clickConsultor();
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(ComiteEliminarDocumentos consulta) {
        consulta.clickConsultor();
    }

    @Step("Acceder al formulario de creación de reunión")
    private void accederFormularioReunion(ComiteCrearReunion crearReunion) {
        crearReunion.flujoAcciones();
    }

    @Step("Ingresar datos de la reunión")
    private void ingresarDatosReunion(ComiteCrearReunion crearReunion) {
        String fechaReunion = DateUtils.getTomorrowDate();
        crearReunion.ingresarDatos(
                testData.reunion.comite,
                testData.reunion.asunto,
                testData.reunion.ubicacion,
                fechaReunion,
                testData.reunion.horaInicio,
                testData.reunion.horaTermino,
                testData.reunion.desc
        );
    }

    @Step("Validar creación de reunión")
    private void validarCreacionReunion(ComiteCrearReunion crearReunion) {
        crearReunion.validarResultado(testData.reunion.asunto);
        crearReunion.valdateFileDownload();
    }

    @Step("Acceder al listado de documentos")
    private void accederListadoDocumentos(ComiteEliminarDocumentos eliminarDocumentos) {
        eliminarDocumentos.flujoAcciones();
    }

    @Step("Eliminar documento")
    private void eliminarDocumento(ComiteEliminarDocumentos eliminarDocumentos) {
        eliminarDocumentos.eliminarDocumento(testData.reunion.documentos.documento);
    }

    @Step("Subir documento de tipo {tipo}")
    private void subirDocumento(String documento, String tipo) {
        ComiteSubirDocumentos subirDocumentos = new ComiteSubirDocumentos(driver);

        guardarPestana();

        hacerClickEnConsultor(subirDocumentos);

        accederFormularioSubida(subirDocumentos, tipo);

        ingresarDatosDocumento(subirDocumentos, documento, tipo);

        guardarYValidarDocumento(subirDocumentos, tipo);

        cerrarPestana();
    }

    @Step("Acceder al formulario para subir documento de tipo {tipo}")
    private void accederFormularioSubida(ComiteSubirDocumentos subirDocumentos, String tipo) {
        subirDocumentos.flujoAcciones();
    }

    @Step("Ingresar datos para documento de tipo {tipo}")
    private void ingresarDatosDocumento(ComiteSubirDocumentos subirDocumentos, String documento, String tipo) {
        subirDocumentos.ingresarDatos(
                testData.reunion.documentos.comite,
                testData.reunion.documentos.reunion,
                documento,
                testData.reunion.documentos.autor
        );
    }

    @Step("Guardar y validar documento de tipo {tipo}")
    private void guardarYValidarDocumento(ComiteSubirDocumentos subirDocumentos, String tipo) {
        subirDocumentos.clickGuardarDocumento();
        switch (tipo) {
            case "Tabla":
                subirDocumentos.validarDocumentoTabla();
                break;
            case "Minuta":
                subirDocumentos.validarDocumentoMinuta();
                break;
            case "Documento":
                subirDocumentos.validarDocumentoDocumento();
                break;
            case "Asistencia":
                subirDocumentos.validarDocumentoAsistencia();
                break;
        }
    }

    @Step("Cerrar la pestaña actual y volver a la original")
    private void cerrarPestana() {
        tabManager.closeAndReturnToOriginalTab();
    }

    @AfterAll
    public static void finalizarFlujo() {
        // Desactivar el flujo continuo
        endContinuousFlow();

        // Si hay una instancia actual, forzar cierre explícito del navegador
        if (currentTestInstance != null) {
            currentTestInstance.forceCloseDriver();
        }
    }
}