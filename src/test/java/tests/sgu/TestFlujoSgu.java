package tests.sgu;

import cl.aach.pages.sgu.AprobarSolicitud;
import cl.aach.pages.sgu.ModificarUsuario;
import cl.aach.pages.sgu.NuevaSolicitud;
import cl.aach.pages.sgu.ValidarEstadoSolicitud;
import cl.aach.utils.LoginStrategyFactory;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import tests.BaseTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("SGU - Gestión de Solicitudes")
public class TestFlujoSgu extends BaseTest {

    private static TestFlujoSgu currentTestInstance;

    @BeforeAll
    public static void iniciarFlujo() {
        // Activamos el flujo continuo para mantener el WebDriver entre tests
        startContinuousFlow();
    }

    @BeforeEach
    public void saveInstance() {
        // Guardar la instancia actual para poder acceder a ella en métodos estáticos
        currentTestInstance = this;
    }

    @Test
    @Order(1)
    @Feature("Nueva Solicitud")
    @Description("SGU - Nueva Solicitud para Activar el perfil DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testCrearNuevaSolicitud() {
        // Usar la estrategia de login SGU
        changeLoginStrategy(LoginStrategyFactory.LoginType.SGU);
        performLogin();

        NuevaSolicitud solicitud = new NuevaSolicitud(driver);

        Allure.step("Acceder al submenú de gestión de usuarios");
        solicitud.clickSubMenu();

        Allure.step("Buscar usuario con RUT: " + testData.credentials.portalAdmin.user);
        solicitud.buscarUsuario(testData.credentials.portalAdmin.user);

        Allure.step("Especificar la solicitud de activación");
        solicitud.especificarSolicitud();

        Allure.step("Enviar solicitud con observaciones");
        solicitud.enviarSolicitud("Solicitud de Habilitación");

        Allure.step("Validar mensaje de confirmación");
        solicitud.validarResultado("La solicitud ha sido notificada.");
    }

    @Test
    @Order(2)
    @Feature("Aprobar Solicitud de Activación")
    @Description("SGU - Aprobar Solicitud para activar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testAprobarSolicitud() {
        // Cambiar a la estrategia de login Portal Admin
        changeLoginStrategy(LoginStrategyFactory.LoginType.PORTAL_ADMIN);
        performLogin();

        AprobarSolicitud aprobar = new AprobarSolicitud(driver);

        Allure.step("Acceder a la gestión de solicitudes");
        aprobar.gestionDeSolicitudes();

        Allure.step("Autorizar solicitud");
        aprobar.autorizarSolicitud();
    }

    @Test
    @Order(3)
    @Feature("Validar Estado")
    @Description("SGU - Validar Estado de Solicitud para activar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testValidarEstadoSolicitud() {
        // Usar la estrategia de login Portal Admin (igual que en Aprobar Solicitud)
        changeLoginStrategy(LoginStrategyFactory.LoginType.PORTAL_ADMIN);
        performLogin();

        ValidarEstadoSolicitud validar = new ValidarEstadoSolicitud(driver);

        Allure.step("Acceder a la gestión de solicitudes y registrar última operación");
        validar.gestionDeSolicitudes();

        Allure.step("Validar que el estado de la solicitud es el esperado");
        validar.validarResultado("DRP PRUEBAS");
    }

    @Test
    @Order(4)
    @Feature("Solicitud de deshabilitación de DRP Pruebas")
    @Description("SGU - Solicitud para Deshabilitar el perfil DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testSolicitudDeshabilitar() {
        // Usar la estrategia de login SGU
        changeLoginStrategy(LoginStrategyFactory.LoginType.SGU);
        performLogin();

        NuevaSolicitud solicitud = new NuevaSolicitud(driver);

        Allure.step("Acceder al submenú de gestión de usuarios");
        solicitud.clickSubMenu();

        Allure.step("Buscar usuario con RUT: " + testData.credentials.portalAdmin.user);
        solicitud.buscarUsuario(testData.credentials.portalAdmin.user);

        Allure.step("Especificar la solicitud de Deshabilitación");
        solicitud.especificarSolicitud();

        Allure.step("Enviar solicitud con observaciones");
        solicitud.enviarSolicitud("Solicitud de deshabilitación");

        Allure.step("Validar mensaje de confirmación");
        solicitud.validarResultado("La solicitud ha sido notificada.");
    }

    @Test
    @Order(5)
    @Feature("Aprobar Solicitud de Deshabilitación")
    @Description("SGU - Aprobar Solicitud para deshabilitar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testAprobarSolicitudDeshabilitacion() {
        // Cambiar a la estrategia de login Portal Admin
        changeLoginStrategy(LoginStrategyFactory.LoginType.PORTAL_ADMIN);
        performLogin();

        AprobarSolicitud aprobar = new AprobarSolicitud(driver);

        Allure.step("Acceder a la gestión de solicitudes");
        aprobar.gestionDeSolicitudes();

        Allure.step("Autorizar solicitud de deshabilitación");
        aprobar.autorizarSolicitud();
    }

    @Test
    @Order(6)
    @Feature("Validar Estado de Deshabilitación")
    @Description("SGU - Validar Estado de Solicitud para desactivar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testValidarEstadoSolicitudDeshabilitacion() {
        // Usar la estrategia de login Portal Admin (igual que en Aprobar Solicitud)
        changeLoginStrategy(LoginStrategyFactory.LoginType.PORTAL_ADMIN);
        performLogin();

        ValidarEstadoSolicitud validar = new ValidarEstadoSolicitud(driver);

        Allure.step("Acceder a la gestión de solicitudes y buscar la última operación");
        validar.gestionDeSolicitudes();

        Allure.step("Validar que el estado de la solicitud es Deshabilitado");
        validar.validarResultado("DRP PRUEBAS");
    }

    @Test
    @Order(7)
    @Feature("Modificar Usuario")
    @Description("SGU - Modificar usuario en el sistema")
    @Severity(SeverityLevel.NORMAL)
    public void modificarUsuario() {
        // Usar la estrategia de login SGU
        changeLoginStrategy(LoginStrategyFactory.LoginType.SGU);
        performLogin();

        ModificarUsuario modificar = new ModificarUsuario(driver);

        Allure.step("Acceder al submenú de gestión de usuarios");
        modificar.clickSubMenu();

        Allure.step("Buscar usuario con RUT: " + testData.credentials.portalAdmin.user);
        modificar.buscarUsuario(testData.credentials.portalAdmin.user);

        Allure.step("Modificar información del usuario");
        modificar.realizarModificacion("Este campo fue modificado con pruebas automatizadas");

        Allure.step("Validar mensaje de confirmación");
        modificar.validarResultado("Usuario modificado.");
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