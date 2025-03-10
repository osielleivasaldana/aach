package tests.sisvf;

import cl.aach.models.TestData;
import cl.aach.pages.sisvf.SisvfConsultaFecNacNombres;
import cl.aach.pages.sisvf.SisvfConsultaRut;
import cl.aach.pages.sisvf.SisvfConsultaNombres;
import cl.aach.utils.TabManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("SISVF")
@Feature("Consultas en SISVF")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoSisvf extends BaseTest {


    @Test
    @Order(1)
    @Description("SISVF - Consulta Fallecidos por Rut")
    @Severity(SeverityLevel.NORMAL)
    public void step_sisvf_rut_en_linea() {
        SisvfConsultaRut consultaRut = new SisvfConsultaRut(driver);

        guardarPestana();
        hacerClickEnConsultor(consultaRut);
        ingresarRut(consultaRut, testData.sisvf.rut);
        validarResultado(consultaRut, testData.sisvf.fecdef);
        cerrarPestana();
    }

    @Test
    @Order(2)
    @Description("SISVF - Consulta Fallecidos por Fecha Nacimiento y Nombre")
    @Severity(SeverityLevel.NORMAL)
    public void step_sisvf_fecnac_nombre_en_linea() {
        SisvfConsultaFecNacNombres consultaFecNac = new SisvfConsultaFecNacNombres(driver);

        guardarPestana();
        hacerClickEnConsultor(consultaFecNac);
        seleccionarTipoDeBusqueda(consultaFecNac);
        ingresarDatosFecNacYNombre(
                consultaFecNac,
                TestData.DateUtils.convertToAaaammdd(testData.sisvf.fecnac),
                testData.sisvf.apellidoPaterno,
                testData.sisvf.apellidoMaterno,
                testData.sisvf.nombres
        );
        validarResultado(consultaFecNac, testData.sisvf.fecdef);
        cerrarPestana();
    }

    @Test
    @Order(3)
    @Description("SISVF - Consulta Fallecidos por Nombres")
    @Severity(SeverityLevel.NORMAL)
    public void step_sisvf_nombres_en_linea() {
        SisvfConsultaNombres consultaNombres = new SisvfConsultaNombres(driver);

        guardarPestana();
        hacerClickEnConsultor(consultaNombres);
        seleccionarTipoDeBusqueda(consultaNombres);
        ingresarDatosNombre(
                consultaNombres,
                testData.sisvf.apellidoPaterno,
                testData.sisvf.apellidoMaterno,
                testData.sisvf.nombres
        );
        validarResultado(consultaNombres, testData.sisvf.fecdef);
        cerrarPestana();
    }

    // Métodos reutilizables

    @Step("Guardar pestaña actual")
    private void guardarPestana() {
        tabManager.saveCurrentTab();
        Allure.step("Pestaña guardada correctamente");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(SisvfConsultaRut consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(SisvfConsultaFecNacNombres consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Hacer clic en el consultor")
    private void hacerClickEnConsultor(SisvfConsultaNombres consulta) {
        consulta.clickConsultor();
        Allure.step("Se hizo clic en el consultor");
    }

    @Step("Seleccionar tipo de búsqueda")
    private void seleccionarTipoDeBusqueda(SisvfConsultaFecNacNombres consulta) {
        consulta.clickTipoDeBusqueda();
        Allure.step("Se seleccionó el tipo de búsqueda");
    }

    @Step("Seleccionar tipo de búsqueda")
    private void seleccionarTipoDeBusqueda(SisvfConsultaNombres consulta) {
        consulta.clickTipoDeBusqueda();
        Allure.step("Se seleccionó el tipo de búsqueda");
    }

    @Step("Ingresar Rut: {rut}")
    private void ingresarRut(SisvfConsultaRut consulta, String rut) {
        consulta.enterRut(rut);
        Allure.step("Se ingresó el Rut: " + rut);
    }

    @Step("Ingresar datos: Apellido Paterno: {apellidoPaterno}, Apellido Materno: {apellidoMaterno}, Nombres: {nombres}")
    private void ingresarDatosNombre(SisvfConsultaNombres consulta, String apellidoPaterno, String apellidoMaterno, String nombres) {
        consulta.enterData(apellidoPaterno, apellidoMaterno, nombres);
        Allure.step(String.format("Se ingresaron los datos: Apellido Paterno: %s, Apellido Materno: %s, Nombres: %s", apellidoPaterno, apellidoMaterno, nombres));
    }

    @Step("Ingresar datos: Fecha Nacimiento: {fechaNac}, Apellido Paterno: {apellidoPaterno}, Apellido Materno: {apellidoMaterno}, Nombres: {nombres}")
    private void ingresarDatosFecNacYNombre(SisvfConsultaFecNacNombres consulta, String fechaNac, String apellidoPaterno, String apellidoMaterno, String nombres) {
        consulta.enterData(fechaNac, apellidoPaterno, apellidoMaterno, nombres);
        Allure.step(String.format("Se ingresaron los datos: Fecha Nacimiento: %s, Apellido Paterno: %s, Apellido Materno: %s, Nombres: %s", fechaNac, apellidoPaterno, apellidoMaterno, nombres));
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SisvfConsultaRut consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SisvfConsultaFecNacNombres consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Validar resultado esperado: {resultadoEsperado}")
    private void validarResultado(SisvfConsultaNombres consulta, String resultadoEsperado) {
        consulta.validateResult(resultadoEsperado);
        Allure.step("Se validó el resultado esperado: " + resultadoEsperado);
    }

    @Step("Cerrar la pestaña actual y volver a la original")
    private void cerrarPestana() {
        tabManager.closeAndReturnToOriginalTab();
        Allure.step("Pestaña cerrada y retorno a la original completado");
    }
}
