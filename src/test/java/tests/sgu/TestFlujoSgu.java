package tests.sgu;

import cl.aach.models.TestData;
import cl.aach.pages.sgu.AprobarSolicitud;
import cl.aach.pages.sgu.ModificarUsuario;
import cl.aach.pages.sgu.NuevaSolicitud;
import cl.aach.pages.sgu.ValidarEstadoSolicitud;
import cl.aach.utils.TestDataLoader;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("SGU - Gestión de Solicitudes")
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoSgu {

    private static TestData testData;

    @BeforeAll
    public static void setUp() {
        testData = TestDataLoader.loadDefaultTestData();
    }

    @Test
    @Order(1)
    @Feature("Nueva Solicitud")
    @Description("SGU - Nueva Solicitud para Activar el perfil DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testCrearNuevaSolicitud() {
        NuevaSolicitud solicitud = new NuevaSolicitud();
        try {
            Allure.step("Abrir la URL del sistema SGU");
            solicitud.abrirUrl();

            Allure.step("Realizar login con usuario SGU");
            solicitud.login(testData.credentials.sgu.user, testData.credentials.sgu.password);

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
        } finally {
            solicitud.close();
        }
    }

    @Test
    @Order(2)
    @Feature("Aprobar Solicitud de Activación")
    @Description("SGU - Aprobar Solicitud para activar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testAprobarSolicitud() {
        AprobarSolicitud aprobar = new AprobarSolicitud();
        try {
            Allure.step("Abrir la URL del sistema SGU");
            aprobar.abrirUrl();

            Allure.step("Realizar login con usuario Admin");
            aprobar.login(testData.credentials.portalAdmin.user, testData.credentials.portalAdmin.password);

            Allure.step("Acceder a la gestión de solicitudes");
            aprobar.gestionDeSolicitudes();

            Allure.step("Autorizar solicitud");
            aprobar.autorizarSolicitud();
        } finally {
            aprobar.close();
        }
    }

    @Test
    @Order(3)
    @Feature("Validar Estado")
    @Description("SGU - Validar Estado de Solicitud para activar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testValidarEstadoSolicitud() {
        ValidarEstadoSolicitud validar = new ValidarEstadoSolicitud();
        try {
            Allure.step("Abrir la URL del sistema SGU");
            validar.abrirUrl();

            Allure.step("Realizar login con usuario Admin");
            validar.login();

            Allure.step("Acceder a la gestión de solicitudes y registrar última operación");
            validar.gestionDeSolicitudes();

            Allure.step("Validar que el estado de la solicitud es el esperado");
            validar.validarResultado("DRP PRUEBAS");
        } finally {
            validar.close();
        }
    }


    //Deshabilitacion de DRP Pruebas

    @Test
    @Order(4)
    @Feature("Solicitud de deshabilitación de DRP Pruebas")
    @Description("SGU - Solicitud para Deshabilitar el perfil DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testSolicitudDeshabilitar() {
        NuevaSolicitud solicitud = new NuevaSolicitud();
        try {
            Allure.step("Abrir la URL del sistema SGU");
            solicitud.abrirUrl();

            Allure.step("Realizar login con usuario SGU");
            solicitud.login(testData.credentials.sgu.user, testData.credentials.sgu.password);

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
        } finally {
            solicitud.close();
        }
    }

    @Test
    @Order(5)
    @Feature("Aprobar Solicitud de Deshabilitación")
    @Description("SGU - Aprobar Solicitud para deshabilitar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testAprobarSolicitudDeshabilitacion() {
        AprobarSolicitud aprobar = new AprobarSolicitud();
        try {
            Allure.step("Abrir la URL del sistema SGU");
            aprobar.abrirUrl();

            Allure.step("Realizar login con usuario Admin");
            aprobar.login(testData.credentials.portalAdmin.user, testData.credentials.portalAdmin.password);

            Allure.step("Acceder a la gestión de solicitudes");
            aprobar.gestionDeSolicitudes();

            Allure.step("Autorizar solicitud de deshabilitación");
            aprobar.autorizarSolicitud();
        } finally {
            aprobar.close();
        }
    }

    @Test
    @Order(6)
    @Feature("Validar Estado de Deshabilitación")
    @Description("SGU - Validar Estado de Solicitud para desactivar DRP Pruebas")
    @Severity(SeverityLevel.NORMAL)
    public void testValidarEstadoSolicitudDeshabilitacion() {
        ValidarEstadoSolicitud validar = new ValidarEstadoSolicitud();
        try {
            Allure.step("Abrir la URL del sistema SGU");
            validar.abrirUrl();

            Allure.step("Realizar login con usuario Admin");
            validar.login();

            Allure.step("Acceder a la gestión de solicitudes y buscar la última operación");
            validar.gestionDeSolicitudes();

            Allure.step("Validar que el estado de la solicitud ses Dehabilitado");
            validar.validarResultado("DRP PRUEBAS");
        } finally {
            validar.close();
        }
    }



    @Test
    @Order(7)
    @Feature("Modificar Usuario")
    @Description("SGU - Modificar usuario en el sistema")
    @Severity(SeverityLevel.NORMAL)
    public void modificarUsuario() {
        ModificarUsuario modificar = new ModificarUsuario();
        try {
            Allure.step("Abrir la URL del sistema SGU");
            modificar.abrirUrl();

            Allure.step("Realizar login con usuario SGU");
            modificar.login(testData.credentials.sgu.user, testData.credentials.sgu.password);

            Allure.step("Acceder al submenú de gestión de usuarios");
            modificar.clickSubMenu();

            Allure.step("Buscar usuario con RUT: " + testData.credentials.portalAdmin.user);
            modificar.buscarUsuario(testData.credentials.portalAdmin.user);

            Allure.step("Modificar información del usuario");
            modificar.realizarModificacion("Este campo fue modificado con pruebas automatizadas");

            Allure.step("Validar mensaje de confirmación");
            modificar.validarResultado("Usuario modificado.");
        } finally {
            modificar.close();
        }
    }
}
