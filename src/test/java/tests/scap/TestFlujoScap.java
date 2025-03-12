package tests.scap;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.scap.ScapConsultas;
import cl.aach.utils.LoginStrategyFactory;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

@Epic("SCAP")
@Feature("Consultas en Sistema Cambio de Apellidos")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.PORTAL) // Especificar estrategia por defecto (Portal)
public class TestFlujoScap extends BaseTest {

    private static TestFlujoScap currentTestInstance;

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
    @Description("SCAP - Consultas por Fecha")
    @Severity(SeverityLevel.NORMAL)
    public void SCAP_Consultas() {
        ScapConsultas consultas = new ScapConsultas(driver);

        guardarPestana();

        hacerClickEnConsultor(consultas);

        buscarRegistros(consultas, testData.scap.fecInicioConsulta);

        validarResultado(consultas, testData.scap.resultadoEsperado);

        cerrarPestana();
    }

    // Métodos intermedios para mejorar la trazabilidad y el registro en Allure

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
        Allure.step("Pestaña guardada correctamente.");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(ScapConsultas consultas) {
        consultas.clickConsultor();
        Allure.step("Se hizo clic en el consultor.");
    }

    @Step("Buscar registros desde la fecha: {fecha}")
    private void buscarRegistros(ScapConsultas consultas, String fecha) {
        consultas.buscarRegistros(fecha);
        Allure.step("Se buscaron registros desde la fecha: " + fecha);
    }

    @Step("Validar el resultado esperado: {resultadoEsperado}")
    private void validarResultado(ScapConsultas consultas, String resultadoEsperado) {
        consultas.ValidarResultado(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Cerrar pestaña y volver a la original")
    private void cerrarPestana() {
        tabManager.closeAndReturnToOriginalTab();
        Allure.step("Pestaña cerrada y retorno a la original completado.");
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