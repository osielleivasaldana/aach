package tests.svm;

import cl.aach.pages.svm.SvmPage;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.BaseTest;

public class TestFlujoSvm {
    @Test

    @Description("SVM - Consulta Bono por JSON")
    @ExtendWith(BaseTest.TestFailureWatcher.class) // Se extiende con el watcher para capturas
    public void step_svm_consulta_bono() {
        // Instanciar SvmPage
        SvmPage svmPage = new SvmPage();

        try {
            // Abrir la URL desde SvmPage
            svmPage.openServiceUrl();

            // Ingresar datos
            svmPage.ingresarDatos("FN02005823", "951233", "422775562");

            // Validar respuesta
            String respuesta = svmPage.validarRespuesta();

        } finally {
            // Cerrar el driver manejado por SvmPage
            svmPage.close();
        }
    }
}
