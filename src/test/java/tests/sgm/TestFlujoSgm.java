package tests.sgm;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.sgm.NuevoEnvio;
import cl.aach.utils.LoginStrategyFactory;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

@Epic("SGM")
@Feature("SGM - NUEVO ENVIO")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.PORTAL) // Especificar estrategia por defecto (Portal)
public class TestFlujoSgm extends BaseTest {

    private static TestFlujoSgm currentTestInstance;

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
    @Description("SGM - NUEVO ENVIO")
    @Severity(SeverityLevel.NORMAL)
    public void SGM_Nuevo_Envio() {
        NuevoEnvio consultas = new NuevoEnvio(driver);

        Allure.step("Hacer clic en el consultor");
        consultas.clickConsultor();

        Allure.step("Acceder al submenú");
        consultas.subMenu();

        Allure.step("Realizar envío con datos de prueba");
        consultas.realizarEnvio(
                testData.sgm.carpeta,
                testData.sgm.libro,
                testData.sgm.referencia,
                testData.sgm.responsableTecnico,
                testData.sgm.responsableTecnicoFinal
        );

        Allure.step("Validar resultado esperado: " + testData.sgm.resultadoEsperado);
        consultas.validarResultado(testData.sgm.resultadoEsperado);
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