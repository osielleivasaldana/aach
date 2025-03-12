package tests.srcei;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.srcei.SrciRvmEnLinea;
import cl.aach.pages.srcei.SrciRvmEnLocal;
import cl.aach.pages.srcei.SrciRutEnLinea;
import cl.aach.utils.LoginStrategyFactory;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

@Epic("SRCEI")
@Feature("Consultas en SRCEI")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.PORTAL) // Especificar estrategia por defecto (Portal)
public class TestFlujoSrcei extends BaseTest {

    private static TestFlujoSrcei currentTestInstance;

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
    @Description("SRCEI - Consulta Rut en línea")
    @Severity(SeverityLevel.NORMAL)
    public void step_srcei_rut_en_linea() {
        SrciRutEnLinea consultaRut = new SrciRutEnLinea(driver);

        iniciarConsulta(consultaRut);
        consultaRut.buscarPorRut("15231738-7");
        cerrarPestana();
    }

    @Test
    @Order(2)
    @Description("SRCEI - Consulta RVM en línea")
    @Severity(SeverityLevel.NORMAL)
    public void step_srcei_rvm_en_linea() {
        try {
            Thread.sleep(1000); // Pequeña pausa entre tests
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SrciRvmEnLinea consultaRvm = new SrciRvmEnLinea(driver);

        iniciarConsulta(consultaRvm);
        consultaRvm.enterRvm(testData.srci.rut);
        cerrarPestana();
    }

    @Test
    @Order(3)
    @Description("SRCEI - Consulta RVM en local")
    @Severity(SeverityLevel.NORMAL)
    public void step_srcei_rvm_en_local() {
        try {
            Thread.sleep(1000); // Pequeña pausa entre tests
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SrciRvmEnLocal consultaRvmLocal = new SrciRvmEnLocal(driver);

        iniciarConsulta(consultaRvmLocal);
        consultaRvmLocal.enterRvm(testData.srci.patente);
        validarResultado(consultaRvmLocal, testData.srci.patente);
        cerrarPestana();
    }

    // Métodos envoltorios reutilizables
    @Step("Iniciar consulta en SRCEI")
    private void iniciarConsulta(SrciRutEnLinea consulta) {
        guardarPestana();
        consulta.abrirModuloSISGEN();
        consulta.navegarAMenuSiniestros();
        consulta.buscarEnLinea();
    }

    @Step("Iniciar consulta en SRCEI")
    private void iniciarConsulta(SrciRvmEnLinea consulta) {
        guardarPestana();
        consulta.clickConsultor();
        consulta.clickMenu();
        consulta.clickBuscarEnLinea();
    }

    @Step("Iniciar consulta en SRCEI")
    private void iniciarConsulta(SrciRvmEnLocal consulta) {
        guardarPestana();
        consulta.clickConsultor();
        consulta.clickMenu();
        consulta.clickBuscarEnLinea();
    }

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
        Allure.step("Pestaña guardada correctamente");
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SrciRvmEnLocal consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Cerrar la pestaña actual y volver a la original")
    private void cerrarPestana() {
        tabManager.closeAndReturnToOriginalTab();
        Allure.step("Pestaña cerrada y retorno a la original completado");
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