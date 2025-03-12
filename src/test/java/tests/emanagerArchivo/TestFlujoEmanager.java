package tests.emanagerArchivo;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.emanager.EliminarArchivo;
import cl.aach.pages.emanager.SubirArchivo;
import cl.aach.pages.emanager.ValidarArchivo;
import cl.aach.utils.DateUtils;
import cl.aach.utils.LoginStrategyFactory;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

/**
 * Test para el flujo completo de gestión de archivos en eManager.
 * Utiliza la estrategia de login de EMANAGER.
 */
@Epic("EMANAGER")
@Feature("Gestión de Archivos en Emanager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.EMANAGER) // Especificamos explícitamente la estrategia de login
public class TestFlujoEmanager extends BaseTest {

    private static TestFlujoEmanager currentTestInstance;

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

    @Test
    @Order(1)
    @Description("EMANAGER - Subir Archivo")
    @Severity(SeverityLevel.CRITICAL)
    public void step_emanager_subir_archivo() {
        SubirArchivo subirArchivo = new SubirArchivo(driver);

        // Configurar la fecha dinámica (mañana)
        String fechaTermino = DateUtils.getTomorrowDate();

        // Ya no necesitamos abrir URL ni hacer login, BaseTest ya lo hizo con la estrategia EMANAGER

        accederMenu(subirArchivo);
        completarFormularioSubirArchivo(subirArchivo, fechaTermino);
    }

    @Test
    @Order(2)
    @Description("EMANAGER - Validar que archivo subió correctamente")
    @Severity(SeverityLevel.NORMAL)
    public void step_emanager_validar_archivo() {
        ValidarArchivo validarArchivo = new ValidarArchivo(driver);

        // Ya no necesitamos abrir URL ni hacer login, BaseTest ya lo hizo

        accederMenu(validarArchivo);
        validarResultadoArchivo(validarArchivo);
    }

    @Test
    @Order(3)
    @Description("EMANAGER - Eliminar archivo")
    @Severity(SeverityLevel.NORMAL)
    public void step_emanager_borrar_archivo() {
        EliminarArchivo eliminarArchivo = new EliminarArchivo(driver);

        // Ya no necesitamos abrir URL ni hacer login, BaseTest ya lo hizo

        accederMenu(eliminarArchivo);
        eliminarNormativa(eliminarArchivo);
    }

    // Métodos comunes reutilizables con pasos para Allure

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