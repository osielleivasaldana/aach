package tests.sgm;


import cl.aach.pages.sgm.NuevoEnvio;
import io.qameta.allure.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

@Epic("SGM")
@Feature("SGM - NUEVO ENVIO")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
public class TestFlujoSgm extends BaseTest {

    @Test
    @Order(1)
    @Description("SGM - NUEVO ENVIO")
    @Severity(SeverityLevel.NORMAL)
    public void SGM_Nuevo_Envio() {
        NuevoEnvio consultas = new NuevoEnvio(driver);
        consultas.clickConsultor();
        consultas.subMenu();
        consultas.realizarEnvio(
                testData.sgm.carpeta,
                testData.sgm.libro,
                testData.sgm.referencia,
                testData.sgm.responsableTecnico,
                testData.sgm.responsableTecnicoFinal
                );
        consultas.validarResultado(testData.sgm.resultadoEsperado);


    }


}