package tests.sgps;

import cl.aach.pages.sgps.AccesoFormularioAprobado;
import cl.aach.utils.RecuperarGps;
import io.qameta.allure.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;


@Epic("SGPS")
@Feature("Flujo Completo de SGPS")
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoSgps {


    @Test
    @Order(1)
    @Description("Ejecutar flujo completo de SGPS")
    @Severity(SeverityLevel.NORMAL)
    public void testFlujoCompleto() {
        try {

            // Ejecutar el acceso al formulario y procesar los datos
            AccesoFormularioAprobado accesoFormulario = new AccesoFormularioAprobado();
            String[] response = accederAlFormulario(accesoFormulario);

            // Procesar los datos obtenidos
            procesarFormulario(response);

            // Recuperar datos desde el servicio RecuperarGPS
            recuperarDatosGps(response[1]);

        } catch (Exception e) {
            manejarError(e);
        }
    }

    // Métodos intermedios para mejorar trazabilidad y registro en Allure


    @Step("Acceder al formulario y obtener los datos")
    private String[] accederAlFormulario(AccesoFormularioAprobado accesoFormulario) throws Exception {
        String[] response = accesoFormulario.accederAlFormulario();
        Allure.step("Formulario accedido correctamente. Datos obtenidos: IdSGPS=" + response[0] + ", Token=" + response[1] + ", DescripcionEstado=" + response[2]);
        return response;
    }

    @Step("Procesar datos obtenidos del formulario")
    private void procesarFormulario(String[] response) {
        String idSGPS = response[0];
        String token = response[1];
        String descripcionEstado = response[2];

        System.out.println("IdSGPS obtenido del formulario: " + idSGPS);
        System.out.println("Token obtenido del formulario: " + token);
        System.out.println("DescripcionEstado del formulario: " + descripcionEstado);

        Allure.step("Datos del formulario procesados: IdSGPS=" + idSGPS + ", Token=" + token + ", DescripcionEstado=" + descripcionEstado);
    }

    @Step("Recuperar datos desde el servicio RecuperarGPS con el token: {token}")
    private void recuperarDatosGps(String token) throws Exception {
        String[] gpsResponse = RecuperarGps.recuperarGps(token);

        String idSGPSRecuperado = gpsResponse[0];
        String descripcionEstadoRecuperado = gpsResponse[1];

        System.out.println("Datos recuperados del servicio RecuperarGPS:");
        System.out.println("IdSGPS: " + idSGPSRecuperado);
        System.out.println("DescripcionEstado: " + descripcionEstadoRecuperado);

        Allure.step("Datos recuperados del servicio RecuperarGPS: IdSGPS=" + idSGPSRecuperado + ", DescripcionEstado=" + descripcionEstadoRecuperado);
    }


    @Step("Manejar error durante la ejecución")
    private void manejarError(Exception e) {
        Allure.step("Error en el flujo de pruebas SGPS: " + e.getMessage());
        System.err.println("Error en el flujo de pruebas SGPS: " + e.getMessage());
        e.printStackTrace();
    }
}
