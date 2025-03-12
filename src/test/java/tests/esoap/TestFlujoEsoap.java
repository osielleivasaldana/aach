package tests.esoap;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.esoap.EsoapConsultaPorPatente;
import cl.aach.pages.esoap.EsoapConsultaPorPoliza;
import cl.aach.pages.esoap.UltimasPolizas;
import cl.aach.utils.LoginStrategyFactory;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

@Epic("ESOAP")
@Feature("Consultas en ESOAP")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.PORTAL) // Especificar estrategia por defecto (Portal)
public class TestFlujoEsoap extends BaseTest {

    private static TestFlujoEsoap currentTestInstance;

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
    @Description("ESOAP - Consulta por Poliza")
    @Severity(SeverityLevel.NORMAL)
    public void stepEsoapConsultaPoliza() {
        EsoapConsultaPorPoliza consultaPoliza = new EsoapConsultaPorPoliza(driver);

        guardarPestana();

        hacerClickEnConsultor(consultaPoliza);

        ingresarPoliza(consultaPoliza, testData.esoap.poliza.poliza);

        validarResultado(consultaPoliza, testData.esoap.poliza.patente);

        cerrarPestana();
    }

    @Test
    @Order(2)
    @Description("ESOAP - Consulta por Patente")
    @Severity(SeverityLevel.NORMAL)
    public void stepEsoapConsultaPatente() {
        EsoapConsultaPorPatente consultaPatente = new EsoapConsultaPorPatente(driver);

        guardarPestana();

        hacerClickEnConsultor(consultaPatente);

        ingresarPatentes(consultaPatente, testData.esoap.patente.patenteA, testData.esoap.patente.patenteB);

        validarResultado(consultaPatente, testData.esoap.patente.poliza);

        cerrarPestana();
    }

    @Test
    @Order(3)
    @Description("ESOAP - Consulta por últimas polizas de compañias")
    @Severity(SeverityLevel.NORMAL)
    public void stepEsoapUltimasPolizas() {
        UltimasPolizas ultimasPolizas = new UltimasPolizas(driver);

        guardarPestana();

        hacerClickEnConsultor(ultimasPolizas);

        buscarUltimasPolizas(ultimasPolizas);

        validarResultado(ultimasPolizas, "Fecha");

        cerrarPestana();
    }

    // Métodos envoltorios reutilizables

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
        Allure.step("Pestaña guardada correctamente");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(EsoapConsultaPorPoliza consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(EsoapConsultaPorPatente consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(UltimasPolizas consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Ingresar póliza: {poliza}")
    private void ingresarPoliza(EsoapConsultaPorPoliza consulta, String poliza) {
        consulta.enterPoliza(poliza);
        Allure.step("Se ingresó la póliza: " + poliza);
    }

    @Step("Ingresar patentes: {patenteA} y {patenteB}")
    private void ingresarPatentes(EsoapConsultaPorPatente consulta, String patenteA, String patenteB) {
        consulta.enterPatente(patenteA, patenteB);
        Allure.step(String.format("Se ingresaron las patentes: %s y %s", patenteA, patenteB));
    }

    @Step("Buscar últimas pólizas")
    private void buscarUltimasPolizas(UltimasPolizas consulta) {
        consulta.buscarPoliza();
        Allure.step("Se buscaron las últimas pólizas");
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(EsoapConsultaPorPoliza consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(EsoapConsultaPorPatente consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(UltimasPolizas consulta, String resultadoEsperado) {
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