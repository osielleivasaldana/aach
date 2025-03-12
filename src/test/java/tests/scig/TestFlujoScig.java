package tests.scig;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.scig.ScigConsultas;
import cl.aach.utils.LoginStrategyFactory;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

@Epic("SCIG")
@Feature("Consultas en SCIG")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.PORTAL) // Especificar estrategia por defecto (Portal)
public class TestFlujoScig extends BaseTest {

    private static TestFlujoScig currentTestInstance;

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
    @Description("SCIG - Consultas por Fecha")
    @Severity(SeverityLevel.NORMAL)
    public void SCIG_Consultas() {
        ScigConsultas consultas = new ScigConsultas(driver);

        guardarPestana();

        hacerClickEnConsultor(consultas);

        buscarRegistros(consultas, testData.scig.fecInicioConsulta);

        validarResultado(consultas, testData.scig.resultadoEsperado);

        cerrarPestana();
    }

    // Métodos intermedios para mejorar la trazabilidad y el registro en Allure

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
        Allure.step("Pestaña guardada correctamente.");
    }


    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(ScigConsultas consulta) {
        String alertMessage = consulta.clickConsultor();
        if (alertMessage != null) {
            Allure.addAttachment("Alerta detectada", "text/plain", alertMessage);
            Allure.step("Verificar acceso al módulo",
                    () -> Assertions.fail("No se pudo acceder al módulo: " + alertMessage));
        }
    }


    @Step("Buscar registros desde la fecha: {fecha}")
    private void buscarRegistros(ScigConsultas consultas, String fecha) {
        consultas.buscarRegistros(fecha);
        Allure.step("Se buscaron registros desde la fecha: " + fecha);
    }

    @Step("Validar el resultado esperado: {resultadoEsperado}")
    private void validarResultado(ScigConsultas consultas, String resultadoEsperado) {
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