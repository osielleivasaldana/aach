package cl.aach.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para manejar operaciones relacionadas con fechas.
 */
public class DateUtils {

    /**
     * Obtiene la fecha de mañana en el formato especificado.
     *
     * @return La fecha de mañana como cadena formateada (dd-MM-yyyy).
     */
    public static String getTomorrowDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return tomorrow.format(formatter);
    }
}
