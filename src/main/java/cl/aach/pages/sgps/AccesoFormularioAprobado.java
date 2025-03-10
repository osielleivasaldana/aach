package cl.aach.pages.sgps;

import cl.aach.utils.AccederFormulario;

public class AccesoFormularioAprobado {

    // ==============================
    // Constructor
    // ==============================
    public AccesoFormularioAprobado() {
        // Constructor vacío
    }

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * Accede al formulario y devuelve el IdSGPS, Token y DescripcionEstado.
     *
     * @return Un array con IdSGPS, Token y DescripcionEstado. En caso de error, se devuelven valores por defecto.
     */
    public String[] accederAlFormulario() {
        try {
            // Datos necesarios para la solicitud
            String rutCia = "81274800"; // Rut de la compañía
            String idTransaccion1 = "PRUEBA AUTOMATIZADA"; // Identificador de la transacción

            // Llamada a la utilidad para acceder al formulario
            String[] result = AccederFormulario.accederFormulario(rutCia, idTransaccion1);

            // Verificar si el resultado es válido (tres elementos)
            if (result.length == 3) {
                System.out.println("IdSGPS: " + result[0]);
                System.out.println("Token: " + result[1]);
                System.out.println("DescripcionEstado: " + result[2]);
                return result;
            } else {
                System.err.println("La respuesta del formulario es incompleta o no válida.");
            }
        } catch (Exception e) {
            System.err.println("Error al acceder al formulario: " + e.getMessage());
            e.printStackTrace();
        }
        // En caso de error, devolver valores por defecto
        return new String[]{"No disponible", "No disponible", "No disponible"};
    }
}
