package tests.scap;

import cl.aach.pages.scap.ScapConsultas;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("SCAP")
@Feature("Consultas en Sistema Cambio de Apellidos")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoScap extends BaseTest {


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
}
