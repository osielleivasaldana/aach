package cl.aach.utils;

import java.io.File;

/**
 * Utilidades relacionadas con la gestión de archivos y descargas.
 */
public class FileUtils {

    private static final String DEFAULT_DOWNLOAD_PATH = System.getProperty("user.home") + "/Downloads";

    /**
     * Verifica si un archivo existe en un directorio específico.
     *
     * @param directoryPath Ruta del directorio donde buscar el archivo.
     * @param fileName      Nombre del archivo a verificar.
     * @return `true` si el archivo existe, `false` en caso contrario.
     */
    public static boolean isFileDownloaded(String directoryPath, String fileName) {
        File file = new File(directoryPath + "/" + fileName);
        return file.exists();
    }

    /**
     * Verifica si un archivo existe en la carpeta predeterminada de descargas.
     *
     * @param fileName Nombre del archivo a verificar.
     * @return `true` si el archivo existe, `false` en caso contrario.
     */
    public static boolean isFileDownloaded(String fileName) {
        return isFileDownloaded(DEFAULT_DOWNLOAD_PATH, fileName);
    }

    /**
     * Limpia un archivo eliminándolo de un directorio específico.
     *
     * @param directoryPath Ruta del directorio donde se encuentra el archivo.
     * @param fileName      Nombre del archivo a eliminar.
     * @return `true` si el archivo fue eliminado, `false` si no existe o no pudo ser eliminado.
     */
    public static boolean cleanDownloadedFile(String directoryPath, String fileName) {
        File file = new File(directoryPath + "/" + fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Limpia un archivo eliminándolo de la carpeta predeterminada de descargas.
     *
     * @param fileName Nombre del archivo a eliminar.
     * @return `true` si el archivo fue eliminado, `false` si no existe o no pudo ser eliminado.
     */
    public static boolean cleanDownloadedFile(String fileName) {
        return cleanDownloadedFile(DEFAULT_DOWNLOAD_PATH, fileName);
    }

    /**
     * Espera a que un archivo sea descargado verificando periódicamente su existencia en un directorio específico.
     *
     * @param directoryPath   Ruta del directorio donde buscar el archivo.
     * @param fileName        Nombre del archivo a verificar.
     * @param timeoutInSeconds Tiempo máximo de espera en segundos.
     * @return `true` si el archivo se descargó dentro del tiempo esperado, `false` en caso contrario.
     */
    public static boolean waitForFileDownload(String directoryPath, String fileName, int timeoutInSeconds) {
        File file = new File(directoryPath + "/" + fileName);
        int waitedTime = 0;

        while (waitedTime < timeoutInSeconds) {
            if (file.exists()) {
                return true;
            }
            try {
                Thread.sleep(1000); // Espera 1 segundo antes de volver a verificar
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            waitedTime++;
        }
        return false;
    }

    /**
     * Espera a que un archivo sea descargado verificando periódicamente su existencia en la carpeta predeterminada de descargas.
     *
     * @param fileName        Nombre del archivo a verificar.
     * @param timeoutInSeconds Tiempo máximo de espera en segundos.
     * @return `true` si el archivo se descargó dentro del tiempo esperado, `false` en caso contrario.
     */
    public static boolean waitForFileDownload(String fileName, int timeoutInSeconds) {
        return waitForFileDownload(DEFAULT_DOWNLOAD_PATH, fileName, timeoutInSeconds);
    }

    /**
     * Crea un directorio si no existe.
     *
     * @param directoryPath Ruta del directorio a crear.
     * @return `true` si el directorio fue creado, `false` si ya existía.
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            return directory.mkdir();
        }
        return false;
    }
}
