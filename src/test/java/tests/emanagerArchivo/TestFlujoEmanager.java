package tests.emanagerArchivo;

import cl.aach.pages.emanager.EliminarArchivo;
import cl.aach.pages.emanager.SubirArchivo;
import cl.aach.pages.emanager.ValidarArchivo;
import cl.aach.utils.DateUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import cl.aach.models.TestData;
import cl.aach.utils.TestDataLoader;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("EMANAGER")
@Feature("Gestión de Archivos en Emanager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoEmanager {

    private static TestData testData;

    public TestFlujoEmanager() {
        if (testData == null) {
            testData = TestDataLoader.loadDefaultTestData();
        }
    }


    @BeforeAll
    public static void setUp() {
        // Cargar datos de prueba usando TestDataLoader
        testData = TestDataLoader.loadDefaultTestData();
    }

    @Test
    @Order(1)
    @Description("EMANAGER - Subir Archivo")
    @Severity(SeverityLevel.CRITICAL)
    public void step_emanager_subir_archivo() {
        SubirArchivo subirArchivo = new SubirArchivo();
        abrirUrl(subirArchivo);

        // Configurar la fecha dinámica (mañana)
        String fechaTermino = DateUtils.getTomorrowDate();

        iniciarSesion(subirArchivo);

        accederMenu(subirArchivo);

        completarFormularioSubirArchivo(subirArchivo, fechaTermino);

        cerrarEmanager(subirArchivo);
    }

    @Test
    @Order(2)
    @Description("EMANAGER - Validar que archivo subió correctamente")
    @Severity(SeverityLevel.NORMAL)
    public void step_emanager_validar_archivo() {
        ValidarArchivo validarArchivo = new ValidarArchivo();
        abrirUrl(validarArchivo);

        iniciarSesion(validarArchivo);

        accederMenu(validarArchivo);

        validarResultadoArchivo(validarArchivo);

        cerrarEmanager(validarArchivo);
    }

    @Test
    @Order(3)
    @Description("EMANAGER - Eliminar archivo")
    @Severity(SeverityLevel.NORMAL)
    public void step_emanager_borrar_archivo() {
        EliminarArchivo eliminarArchivo = new EliminarArchivo();
        abrirUrl(eliminarArchivo);

        iniciarSesion(eliminarArchivo);

        accederMenu(eliminarArchivo);

        eliminarNormativa(eliminarArchivo);

        cerrarEmanager(eliminarArchivo);
    }

    // Métodos comunes reutilizables con pasos para Allure

    @Step("Abrir la URL del módulo Emanager")
    private void abrirUrl(SubirArchivo subirArchivo) {
        subirArchivo.abrirUrl();
        Allure.step("Se abrió la URL de Emanager");
    }

    @Step("Abrir la URL del módulo Emanager")
    private void abrirUrl(ValidarArchivo validarArchivo) {
        validarArchivo.abrirUrl();
        Allure.step("Se abrió la URL de Emanager");
    }

    @Step("Abrir la URL del módulo Emanager")
    private void abrirUrl(EliminarArchivo eliminarArchivo) {
        eliminarArchivo.abrirUrl();
        Allure.step("Se abrió la URL de Emanager");
    }

    @Step("Iniciar sesión en Emanager")
    private void iniciarSesion(SubirArchivo subirArchivo) {
        subirArchivo.login(testData.credentials.emanager.user, testData.credentials.emanager.password);
        Allure.step("Se inició sesión con usuario: " + testData.credentials.emanager.user);
    }

    @Step("Iniciar sesión en Emanager")
    private void iniciarSesion(ValidarArchivo validarArchivo) {
        validarArchivo.login(testData.credentials.emanager.user, testData.credentials.emanager.password);
        Allure.step("Se inició sesión con usuario: " + testData.credentials.emanager.user);
    }

    @Step("Iniciar sesión en Emanager")
    private void iniciarSesion(EliminarArchivo eliminarArchivo) {
        eliminarArchivo.login(testData.credentials.emanager.user, testData.credentials.emanager.password);
        Allure.step("Se inició sesión con usuario: " + testData.credentials.emanager.user);
    }

    @Step("Acceder al menú del módulo Emanager")
    private void accederMenu(SubirArchivo subirArchivo) {
        subirArchivo.clickMenu();
        Allure.step("Se accedió al menú de Emanager");
    }

    @Step("Acceder al menú del módulo Emanager")
    private void accederMenu(ValidarArchivo validarArchivo) {
        validarArchivo.clickMenu();
        Allure.step("Se accedió al menú de Emanager");
    }

    @Step("Acceder al menú del módulo Emanager")
    private void accederMenu(EliminarArchivo eliminarArchivo) {
        eliminarArchivo.clickMenu();
        Allure.step("Se accedió al menú de Emanager");
    }

    @Step("Completar formulario para subir archivo")
    private void completarFormularioSubirArchivo(SubirArchivo subirArchivo, String fechaTermino) {
        subirArchivo.clickAgregar();
        subirArchivo.completarFormularioSubirarchivo(
                testData.emanager.archivo.titulo,
                testData.emanager.archivo.desc,
                testData.emanager.archivo.materia,
                testData.emanager.archivo.prensa,
                testData.emanager.archivo.archivo,
                fechaTermino
        );
        Allure.step("Formulario completado con título: " + testData.emanager.archivo.titulo + ", fecha término: " + fechaTermino);
    }

    @Step("Validar que el archivo subió correctamente")
    private void validarResultadoArchivo(ValidarArchivo validarArchivo) {
        validarArchivo.validarResultado(testData.emanager.archivo.titulo);
        Allure.step("Se validó que el archivo subió correctamente con título: " + testData.emanager.archivo.titulo);
    }

    @Step("Eliminar archivo")
    private void eliminarNormativa(EliminarArchivo eliminarArchivo) {
        eliminarArchivo.eliminarNormativa();
        Allure.step("Se eliminó el archivo correctamente");
    }

    @Step("Cerrar el módulo Emanager")
    private void cerrarEmanager(SubirArchivo subirArchivo) {
        subirArchivo.close();
        Allure.step("Se cerró el módulo Emanager");
    }

    @Step("Cerrar el módulo Emanager")
    private void cerrarEmanager(ValidarArchivo validarArchivo) {
        validarArchivo.close();
        Allure.step("Se cerró el módulo Emanager");
    }

    @Step("Cerrar el módulo Emanager")
    private void cerrarEmanager(EliminarArchivo eliminarArchivo) {
        eliminarArchivo.close();
        Allure.step("Se cerró el módulo Emanager");
    }
}
