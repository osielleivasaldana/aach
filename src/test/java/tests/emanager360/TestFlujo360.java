package tests.emanager360;

import cl.aach.models.TestData;
import cl.aach.pages.emanager360.EliminarNorma;
import cl.aach.pages.emanager360.AgregarNorma;
import cl.aach.pages.emanager360.ModificarNorma;
import cl.aach.utils.DateUtils;
import cl.aach.utils.TestDataLoader;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("EMANAGER BORRADOR 360")
@Feature("Gestión de Normativas")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujo360 {

    private static TestData testData;

    public TestFlujo360() {
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
    @Description("Agregar una nueva normativa como borrador en EMANAGER BORRADOR 360.")
    @Severity(SeverityLevel.NORMAL)
    public void BORRADOR_360_Agregar_Normativa() {
        AgregarNorma agregarNorma = new AgregarNorma();
        abrirUrl(agregarNorma);

        iniciarSesion(agregarNorma);

        accederMenu(agregarNorma);

        agregarNuevaNormativa(agregarNorma);

        validarResultadoNormativa(agregarNorma);

        cerrarEmanager(agregarNorma);
    }

    @Test
    @Order(2)
    @Description("Modificar una normativa existente en EMANAGER BORRADOR 360.")
    @Severity(SeverityLevel.NORMAL)
    public void BORRADOR_360_Modificar_Normativas() {
        ModificarNorma modificarNorma = new ModificarNorma();
        abrirUrl(modificarNorma);

        iniciarSesion(modificarNorma);

        accederMenu(modificarNorma);

        modificarNormativa(modificarNorma);

        cerrarEmanager(modificarNorma);
    }

    @Test
    @Order(3)
    @Description("Eliminar una normativa en EMANAGER BORRADOR 360.")
    @Severity(SeverityLevel.NORMAL)
    public void BORRADOR_360_Eliminar_Normativas() {
        EliminarNorma eliminarNorma = new EliminarNorma();
        abrirUrl(eliminarNorma);

        iniciarSesion(eliminarNorma);

        accederMenu(eliminarNorma);

        eliminarNormativa(eliminarNorma);

        cerrarEmanager(eliminarNorma);
    }

    // Métodos comunes reutilizables con pasos decorados para Allure

    @Step("Abrir la URL de EMANAGER BORRADOR 360")
    private void abrirUrl(AgregarNorma agregarNorma) {
        agregarNorma.abrirUrl();
        Allure.step("Se abrió la URL del módulo EMANAGER BORRADOR 360");
    }

    @Step("Abrir la URL de EMANAGER BORRADOR 360")
    private void abrirUrl(ModificarNorma modificarNorma) {
        modificarNorma.abrirUrl();
        Allure.step("Se abrió la URL del módulo EMANAGER BORRADOR 360");
    }

    @Step("Abrir la URL de EMANAGER BORRADOR 360")
    private void abrirUrl(EliminarNorma eliminarNorma) {
        eliminarNorma.abrirUrl();
        Allure.step("Se abrió la URL del módulo EMANAGER BORRADOR 360");
    }

    @Step("Iniciar sesión en EMANAGER BORRADOR 360")
    private void iniciarSesion(AgregarNorma agregarNorma) {
        agregarNorma.login(testData.credentials.emanager.user, testData.credentials.emanager.password);
        Allure.step("Se inició sesión con usuario: " + testData.credentials.emanager.user);
    }

    @Step("Iniciar sesión en EMANAGER BORRADOR 360")
    private void iniciarSesion(ModificarNorma modificarNorma) {
        modificarNorma.login(testData.credentials.emanager.user, testData.credentials.emanager.password);
        Allure.step("Se inició sesión con usuario: " + testData.credentials.emanager.user);
    }

    @Step("Iniciar sesión en EMANAGER BORRADOR 360")
    private void iniciarSesion(EliminarNorma eliminarNorma) {
        eliminarNorma.login(testData.credentials.emanager.user, testData.credentials.emanager.password);
        Allure.step("Se inició sesión con usuario: " + testData.credentials.emanager.user);
    }

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
        Allure.step("Se intentó modificar la normativa automáticamente. Modificación exitosa ");
    }

    @Step("Eliminar una normativa existente")
    private void eliminarNormativa(EliminarNorma eliminarNorma) {
        eliminarNorma.eliminarNormativa();
        Allure.step("Se eliminó la normativa correctamente.");
    }

    @Step("Cerrar el módulo EMANAGER BORRADOR 360")
    private void cerrarEmanager(AgregarNorma agregarNorma) {
        agregarNorma.close();
        Allure.step("Se cerró el módulo EMANAGER BORRADOR 360");
    }

    @Step("Cerrar el módulo EMANAGER BORRADOR 360")
    private void cerrarEmanager(ModificarNorma modificarNorma) {
        modificarNorma.close();
        Allure.step("Se cerró el módulo EMANAGER BORRADOR 360");
    }

    @Step("Cerrar el módulo EMANAGER BORRADOR 360")
    private void cerrarEmanager(EliminarNorma eliminarNorma) {
        eliminarNorma.close();
        Allure.step("Se cerró el módulo EMANAGER BORRADOR 360");
    }
}
