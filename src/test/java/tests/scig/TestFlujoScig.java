package tests.scig;

import cl.aach.pages.scig.ScigConsultas;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("SCIG")
@Feature("Consultas en SCIG")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoScig extends BaseTest {


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
    private void hacerClickEnConsultor(ScigConsultas consultas) {
        consultas.clickConsultor();
        Allure.step("Se hizo clic en el consultor.");
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
}
