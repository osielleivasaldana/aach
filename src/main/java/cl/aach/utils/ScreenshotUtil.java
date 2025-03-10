package cl.aach.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    public static void takeScreenshot(WebDriver driver, String testName) {
        // Crear formato de fecha y hora para nombre único de archivo
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotName = testName + "_" + timestamp + ".png";

        // Obtener captura de pantalla
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File("target/screenshots/" + screenshotName);

        try {
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("Captura guardada en: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al guardar la captura de pantalla: " + e.getMessage());
        }
    }
}
