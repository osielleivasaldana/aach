package tests.emanager360;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.pages.emanager360.EliminarNorma;
import cl.aach.pages.emanager360.AgregarNorma;
import cl.aach.pages.emanager360.ModificarNorma;
import cl.aach.utils.DateUtils;
import cl.aach.utils.LoginStrategyFactory;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

/**
 * Test para el flujo completo de gestión de normativas en eManager 360.
 * Utiliza la estrategia de login de EMANAGER.
 */
@Epic("EMANAGER BORRADOR 360")
@Feature("Gestión de Normativas")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UseLoginStrategy(LoginStrategyFactory.LoginType.EMANAGER) // Especificamos explícitamente la estrategia de login
public class TestFlujo360 extends BaseTest {

    private static TestFlujo360 currentTestInstance;

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
    @Description("Agregar una nueva normativa como borrador en EMANAGER BORRADOR 360.")
    @Severity(SeverityLevel.NORMAL)
    public void BORRADOR_360_Agregar_Normativa() {
        AgregarNorma agregarNorma = new AgregarNorma(driver);

        // Ya no necesitamos abrir URL ni hacer login, BaseTest ya lo hizo
        accederMenu(agregarNorma);
        agregarNuevaNormativa(agregarNorma);
        validarResultadoNormativa(agregarNorma);

        // No cerramos el navegador, BaseTest lo hará en tearDown
    }

    @Test
    @Order(2)
    @Description("Modificar una normativa existente en EMANAGER BORRADOR 360.")
    @Severity(SeverityLevel.NORMAL)
    public void BORRADOR_360_Modificar_Normativas() {
        ModificarNorma modificarNorma = new ModificarNorma(driver);

        // Ya no necesitamos abrir URL ni hacer login, BaseTest ya lo hizo
        accederMenu(modificarNorma);
        modificarNormativa(modificarNorma);

        // No cerramos el navegador, BaseTest lo hará en tearDown
    }

    @Test
    @Order(3)
    @Description("Eliminar una normativa en EMANAGER BORRADOR 360.")
    @Severity(SeverityLevel.NORMAL)
    public void BORRADOR_360_Eliminar_Normativas() {
        EliminarNorma eliminarNorma = new EliminarNorma(driver);

        // Ya no necesitamos abrir URL ni hacer login, BaseTest ya lo hizo
        accederMenu(eliminarNorma);
        eliminarNormativa(eliminarNorma);

        // No cerramos el navegador, BaseTest lo hará en tearDown
    }

    // Métodos comunes reutilizables con pasos decorados para Allure

    @Step("Acceder al menú principal del módulo EMANAGER BORRADOR 360")
    private void accederMenu(AgregarNorma agregarNorma) {
        agregarNorma.clickMenu();
        Allure.step("Se accedió al menú principal de EMANAGER BORRADOR 360");
    }

    @Step("Acceder al menú principal del módulo EMANAGER BORRADOR 360")
    private void accederMenu(ModificarNorma modificarNorma) {
        modificarNorma.clickMenu();
        Allure.step("Se accedió al menú principal de EMANAGER BORRADOR 360");
    }

    @Step("Acceder al menú principal del módulo EMANAGER BORRADOR 360")
    private void accederMenu(EliminarNorma eliminarNorma) {
        eliminarNorma.clickMenu();
        Allure.step("Se accedió al menú principal de EMANAGER BORRADOR 360");
    }

    @Step("Agregar una nueva normativa como borrador")
    private void agregarNuevaNormativa(AgregarNorma agregarNorma) {
        String fechaReunion = DateUtils.getTomorrowDate();
        agregarNorma.clickAgregar();
        agregarNorma.agregarBorrador(
                testData.emanager.normativa.nombre,
                fechaReunion,
                fechaReunion,
                testData.emanager.normativa.materia
        );
        Allure.step("Se agregó una nueva normativa con nombre: " + testData.emanager.normativa.nombre);
    }

    @Step("Validar que la normativa se agregó correctamente")
    private void validarResultadoNormativa(AgregarNorma agregarNorma) {
        agregarNorma.validarResultado(testData.emanager.normativa.resultado);
        Allure.step("Se validó que la normativa se agregó correctamente con resultado esperado: " + testData.emanager.normativa.resultado);
    }

    @Step("Modificar una normativa automáticamente")
    private void modificarNormativa(ModificarNorma modificarNorma) {
        boolean modificacionExitosa = modificarNorma.modificarNormativaAutomatica("Cerrado");
        Allure.step("Se intentó modificar la normativa automáticamente. Modificación exitosa: " + modificacionExitosa);
    }

    @Step("Eliminar una normativa existente")
    private void eliminarNormativa(EliminarNorma eliminarNorma) {
        eliminarNorma.eliminarNormativa();
        Allure.step("Se eliminó la normativa correctamente.");
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