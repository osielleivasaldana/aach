package tests.sisgen;

import cl.aach.pages.sisgen.SisgenConsultaPorPatente;
import cl.aach.pages.sisgen.SisgenConsultaPorRut;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("SISGEN")
@Feature("Consultas en SISGEN")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoSisgen extends BaseTest {

    @Test
    @Order(1)
    @Description("SISGEN - Consulta por Rut")
    @Severity(SeverityLevel.NORMAL)
    public void SISGEN_Consulta_Rut() {
        SisgenConsultaPorRut consultaRut = new SisgenConsultaPorRut(driver);

        // Ejecutar los pasos y capturarlos en el reporte
        guardarPestana();

        hacerClickEnConsultor(consultaRut);

        hacerClickEnMenu(consultaRut);

        ingresarRut(consultaRut, testData.sisgen.rut);

        validarResultado(consultaRut, testData.sisgen.resultadoRut);

        cerrarPestana();
    }

    @Test
    @Order(2)
    @Description("SISGEN - Consulta por Patente")
    @Severity(SeverityLevel.NORMAL)
    public void SISGEN_Consulta_Patente() {
        SisgenConsultaPorPatente consultaPatente = new SisgenConsultaPorPatente(driver);

        guardarPestana();

        hacerClickEnConsultor(consultaPatente);

        hacerClickEnMenu(consultaPatente);

        ingresarPatentes(consultaPatente, testData.sisgen.patenteA, testData.sisgen.patenteB);

        validarResultado(consultaPatente, testData.sisgen.resultadoPatente);

        cerrarPestana();
    }

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
        // Opcional: Agregar verificación
        Allure.step("Pestaña guardada correctamente");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(SisgenConsultaPorRut consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(SisgenConsultaPorPatente consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Hacer clic en el menú")
    private void hacerClickEnMenu(SisgenConsultaPorRut consulta) {
        consulta.clickMenu();
        Allure.step("Se hizo clic en el menú");
    }

    @Step("Hacer clic en el menú")
    private void hacerClickEnMenu(SisgenConsultaPorPatente consulta) {
        consulta.clickMenu();
        Allure.step("Se hizo clic en el menú");
    }

    @Step("Ingresar Rut: {rut}")
    private void ingresarRut(SisgenConsultaPorRut consulta, String rut) {
        consulta.enterRut(rut);
        Allure.step("Se ingresó el RUT: " + rut);
    }

    @Step("Ingresar Patentes: {patenteA} y {patenteB}")
    private void ingresarPatentes(SisgenConsultaPorPatente consulta, String patenteA, String patenteB) {
        consulta.enterPatente(patenteA, patenteB);
        Allure.step(String.format("Se ingresaron las patentes: %s y %s", patenteA, patenteB));
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SisgenConsultaPorRut consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SisgenConsultaPorPatente consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Cerrar la pestaña actual y volver a la original")
    private void cerrarPestana() {
        tabManager.closeAndReturnToOriginalTab();
        Allure.step("Pestaña cerrada y retorno a la original completado");
    }
}
