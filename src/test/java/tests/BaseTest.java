package tests;

import cl.aach.models.TestData;
import cl.aach.pages.LoginPage;
import cl.aach.utils.ConfigUtil;
import cl.aach.utils.TabManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@ExtendWith(AllureJunit5.class)
@ExtendWith(BaseTest.TestFailureWatcher.class)
public class BaseTest {
    protected WebDriver driver;
    protected static TestData testData;
    protected TabManager tabManager; // Se instancia en setUp()
    private File tempUserDataDir; // Para almacenar la referencia al directorio temporal

    @BeforeAll
    public static void globalSetUp() {
        // Configurar WebDriverManager para la versión correcta de ChromeDriver
        WebDriverManager.chromedriver().setup();
        // Cargar los datos de prueba una sola vez
        if (testData == null) {
            loadTestData();
        }
    }

    private static void loadTestData() {
        try (InputStream inputStream = BaseTest.class.getClassLoader().getResourceAsStream("testData.json")) {
            if (inputStream == null) {
                throw new RuntimeException("No se encontró testData.json en resources.");
            }
            testData = new ObjectMapper().readValue(inputStream, TestData.class);
            System.out.println("Datos de prueba cargados correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar testData.json", e);
        }
    }

    @BeforeEach
    public void setUp() {
        // Crear un directorio temporal único para user-data-dir
        try {
            String uniqueId = UUID.randomUUID().toString();
            tempUserDataDir = Files.createTempDirectory("chrome-user-data-" + uniqueId).toFile();
            System.out.println("Directorio de datos del usuario temporal creado en: " + tempUserDataDir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error al crear directorio temporal: " + e.getMessage());
            // Continuar sin directorio personalizado si hay error
            tempUserDataDir = null;
        }

        // Configurar ChromeOptions
        ChromeOptions options = new ChromeOptions();

        // Agregar argumentos básicos de Chrome
        options.addArguments(
                "--disable-blink-features=AutomationControlled",
                "--disable-extensions",
                "--no-sandbox",
                "--disable-popup-blocking",
                "--start-maximized",
                "--disable-infobars",
                "--disable-browser-side-navigation",
                "--disable-dev-shm-usage"
        );

        // Configurar directorio de usuario único si se pudo crear
        if (tempUserDataDir != null) {
            options.addArguments("--user-data-dir=" + tempUserDataDir.getAbsolutePath());
        }

        // Configurar preferencias
        options.addArguments("--window-size=1920,1080");

        // Crear un mapa para las preferencias
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        options.setExperimentalOption("useAutomationExtension", false);

        // Configurar modo headless si es necesario
        if (Boolean.parseBoolean(System.getenv("HEADLESS"))) {
            options.addArguments("--headless", "--disable-gpu");
        }

        // Crear nueva instancia de WebDriver para cada test
        driver = new ChromeDriver(options);
        TestFailureWatcher.setDriver(driver);

        // Inicializar TabManager con la instancia de driver
        tabManager = new TabManager(driver);

        // Realizar login en la aplicación
        String url = Objects.requireNonNull(ConfigUtil.getProperty("server.url"), "URL del servidor no definida");
        driver.get(url);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(testData.credentials.portalUser.user, testData.credentials.portalUser.password);
        if (driver.manage().getCookies().isEmpty()) {
            throw new RuntimeException("Error en login: No se estableció sesión.");
        }
        System.out.println("Login exitoso. Sesión activa.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            System.out.println("Cerrando navegador...");
            driver.quit();
        }
        TestFailureWatcher.setDriver(null);

        // Limpiar el directorio temporal después de cada prueba
        if (tempUserDataDir != null && tempUserDataDir.exists()) {
            try {
                deleteDirectory(tempUserDataDir);
                System.out.println("Directorio temporal eliminado: " + tempUserDataDir.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error al eliminar directorio temporal: " + e.getMessage());
            }
        }
    }

    // Método recursivo para eliminar directorios
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    /**
     * TestWatcher para capturar capturas de pantalla en caso de fallo.
     */
    public static class TestFailureWatcher implements TestWatcher {
        private static WebDriver driverInstance;

        public static void setDriver(WebDriver driver) {
            driverInstance = driver;
        }

        @Override
        public void testFailed(org.junit.jupiter.api.extension.ExtensionContext context, Throwable cause) {
            if (driverInstance != null) {
                try {
                    // Intentar cerrar cualquier alerta antes de tomar captura
                    Alert alert = driverInstance.switchTo().alert();
                    System.out.println("Cerrando alerta: " + alert.getText());
                    alert.accept();
                } catch (NoAlertPresentException e) {
                    // No hay alerta, continuar normalmente
                }
                takeScreenshot(driverInstance, context.getDisplayName());
            }
        }

        @Attachment(value = "Captura de pantalla en error", type = "image/png")
        public static byte[] takeScreenshot(WebDriver driver, String testName) {
            if (driver == null) {
                return null;
            }
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            saveScreenshotToFile(screenshotBytes, testName);
            return screenshotBytes;
        }

        private static void saveScreenshotToFile(byte[] screenshot, String testName) {
            try {
                Files.createDirectories(Paths.get("target/screenshots/"));
                Files.write(Paths.get("target/screenshots/" + testName + ".png"), screenshot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void testSuccessful(org.junit.jupiter.api.extension.ExtensionContext context) {
            System.out.println("Test pasado: " + context.getDisplayName());
        }
    }
}