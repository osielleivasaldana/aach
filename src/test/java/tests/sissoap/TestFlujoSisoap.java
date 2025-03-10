package tests.sissoap;

import cl.aach.pages.sissoap.SissoapConsultaPorRut;
import cl.aach.pages.sissoap.SissoapConsultaPorPatente;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("SISSOAP")
@Feature("Consultas en SISSOAP")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoSisoap extends BaseTest {

    @Test
    @Order(1)
    @Description("SISSOAP - Consulta por Rut")
    @Severity(SeverityLevel.NORMAL)
    public void stepSissoapConsultaRut() {
        SissoapConsultaPorRut consultaRut = new SissoapConsultaPorRut(driver);

        guardarPestana();

        hacerClickEnConsultor(consultaRut);

        ingresarRut(consultaRut, testData.sissoap.rut.rut);

        validarResultado(consultaRut, testData.sissoap.rut.compania);

        cerrarPestana();
    }

    @Test
    @Order(2)
    @Description("SISSOAP - Consulta por Patente")
    @Severity(SeverityLevel.NORMAL)
    public void stepSissoapConsultaPatente() {
        SissoapConsultaPorPatente consultaPatente = new SissoapConsultaPorPatente(driver);

        guardarPestana();

        hacerClickEnConsultor(consultaPatente);

        ingresarPatente(consultaPatente, testData.sissoap.patente.patente);

        validarResultado(consultaPatente, testData.sissoap.patente.siniestro);

        cerrarPestana();
    }

    // Métodos envoltorios reutilizables

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
        Allure.step("Pestaña guardada correctamente");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(SissoapConsultaPorRut consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(SissoapConsultaPorPatente consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Ingresar Rut: {rut}")
    private void ingresarRut(SissoapConsultaPorRut consulta, String rut) {
        consulta.enterRut(rut);
        Allure.step("Se ingresó el Rut: " + rut);
    }

    @Step("Ingresar Patente: {patente}")
    private void ingresarPatente(SissoapConsultaPorPatente consulta, String patente) {
        consulta.enterPatente(patente);
        Allure.step("Se ingresó la patente: " + patente);
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SissoapConsultaPorRut consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SissoapConsultaPorPatente consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Cerrar la pestaña actual y volver a la original")
    private void cerrarPestana() {
        tabManager.closeAndReturnToOriginalTab();
        Allure.step("Pestaña cerrada y retorno a la original completado");
    }
}
