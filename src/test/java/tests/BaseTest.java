package tests;

import cl.aach.annotations.UseLoginStrategy;
import cl.aach.models.TestData;
import cl.aach.utils.LoginStrategy;
import cl.aach.utils.LoginStrategyFactory;
import cl.aach.utils.TabManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterAll;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase base para todos los tests. Proporciona funcionalidades comunes como:
 * - Gestión del ciclo de vida del WebDriver
 * - Carga de datos de prueba
 * - Estrategias de login
 * - Capturas de pantalla en caso de fallos
 * - Soporte para flujo continuo de tests
 */
@ExtendWith(AllureJunit5.class)
@ExtendWith(BaseTest.TestFailureWatcher.class)
public class BaseTest {
    protected WebDriver driver;
    protected static TestData testData;
    protected TabManager tabManager;
    private File tempUserDataDir;
    protected LoginStrategy loginStrategy;

    // Flag para indicar si estamos en un flujo continuo de tests
    private static volatile boolean continuousFlow = false;

    // Registro de todas las instancias de WebDriver creadas (para limpieza global)
    private static final Map<String, WebDriver> activeDrivers = new ConcurrentHashMap<>();

    // ID único para identificar cada instancia de WebDriver
    private String driverId;

    // Referencia estática para acceder al WebDriver desde métodos estáticos
    private static WebDriver currentDriver;

    // Runtime hook para asegurar la limpieza de recursos al finalizar JVM
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook ejecutándose, limpiando " + activeDrivers.size() + " recurso(s) de navegador...");
            closeAllDrivers();
        }));
    }

    /**
     * Configuración global que se ejecuta una vez antes de todos los tests.
     * Configura WebDriverManager y carga los datos de prueba.
     */
    @BeforeAll
    public static void globalSetUp() {
        // Configurar WebDriverManager para la versión correcta de ChromeDriver
        WebDriverManager.chromedriver().setup();
        // Cargar los datos de prueba una sola vez
        if (testData == null) {
            loadTestData();
        }
    }

    /**
     * Carga los datos de prueba desde el archivo JSON de recursos.
     */
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

    /**
     * Configuración que se ejecuta antes de cada test.
     * Inicializa el WebDriver si es necesario y configura la estrategia de login.
     */
    @BeforeEach
    public void setUp() {
        // Si estamos en un flujo continuo y el driver ya existe, no creamos uno nuevo
        if (continuousFlow && driver != null && isDriverActive(driver)) {
            System.out.println("Reutilizando WebDriver existente para flujo continuo de tests.");
            TestFailureWatcher.setDriver(driver); // Aseguramos que el watcher tenga la referencia correcta
            setupLoginStrategy(); // Inicializamos la estrategia de login
            return;
        }

        System.out.println("Creando nuevo WebDriver...");
        setupChromeDriver();
        // Guardar referencia estática al driver actual
        currentDriver = driver;
        TestFailureWatcher.setDriver(driver);
        setupLoginStrategy();
        // No hacemos login automáticamente aquí para permitir cambios de estrategia
        // El login lo hará cada test explícitamente cuando cambie la estrategia
    }

    /**
     * Verifica si un WebDriver está activo y utilizable
     * @param webDriver el WebDriver a verificar
     * @return true si está activo, false en caso contrario
     */
    private boolean isDriverActive(WebDriver webDriver) {
        if (webDriver == null) return false;

        try {
            // Intenta una operación simple para verificar si el driver está activo
            webDriver.getCurrentUrl();
            return true;
        } catch (Exception e) {
            System.err.println("El WebDriver no está activo o ha sido cerrado: " + e.getMessage());
            // Asegurar limpieza de referencias
            driver = null;
            if (driverId != null) {
                activeDrivers.remove(driverId);
            }
            return false;
        }
    }

    /**
     * Configura y crea una nueva instancia de ChromeDriver.
     */
    private void setupChromeDriver() {
        // Crear un directorio temporal único para user-data-dir
        try {
            String uniqueId = UUID.randomUUID().toString();
            driverId = uniqueId; // Guardar el ID para rastreo

            tempUserDataDir = Files.createTempDirectory("chrome-user-data-" + uniqueId).toFile();
            System.out.println("Directorio de datos del usuario temporal creado en: " + tempUserDataDir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error al crear directorio temporal: " + e.getMessage());
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

        // Configurar tamaño de ventana
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

        // Crear nueva instancia de WebDriver
        this.driver = new ChromeDriver(options);

        // Registrar driver en la colección de drivers activos
        activeDrivers.put(driverId, driver);
        System.out.println("Nuevo WebDriver registrado con ID: " + driverId);

        // Inicializar TabManager
        this.tabManager = new TabManager(driver);
    }

    /**
     * Determina e inicializa la estrategia de login apropiada para el test actual.
     * Puede basarse en anotaciones explícitas o en convenciones de nombres.
     */
    protected void setupLoginStrategy() {
        // Determinar la estrategia de login basada en anotaciones o nombre de clase
        Class<?> testClass = this.getClass();

        // Usar el método mejorado para obtener la estrategia
        // Por defecto, usar PORTAL para casos que no tengan otra lógica específica
        this.loginStrategy = LoginStrategyFactory.getStrategyForTest(
                testClass, testData, LoginStrategyFactory.LoginType.PORTAL);

        System.out.println("Estrategia de login seleccionada: " + loginStrategy.getStrategyName());
    }

    /**
     * Cambia la estrategia de login durante la ejecución.
     * Útil para flujos de prueba que necesitan acceder a diferentes sistemas.
     *
     * @param loginType el tipo de login a usar
     */
    protected void changeLoginStrategy(LoginStrategyFactory.LoginType loginType) {
        this.loginStrategy = LoginStrategyFactory.createLoginStrategy(loginType, testData);
        System.out.println("Cambiando estrategia de login a: " + loginStrategy.getStrategyName());
    }

    /**
     * Activa el modo de flujo continuo para evitar que se cierre el WebDriver entre tests.
     */
    protected static void startContinuousFlow() {
        continuousFlow = true;
        System.out.println("Flujo continuo de tests activado - WebDriver se mantendrá entre tests.");
    }

    /**
     * Desactiva el modo de flujo continuo y permite que se cierre el WebDriver normalmente.
     */
    protected static void endContinuousFlow() {
        System.out.println("Flujo continuo de tests desactivado - WebDriver se cerrará normalmente.");
        continuousFlow = false;
        // Cerrar el WebDriver actual si existe
        closeCurrentDriver();
    }

    /**
     * Cierra el WebDriver actual si existe (método estático para usar en @AfterAll)
     */
    protected static void closeCurrentDriver() {
        if (currentDriver != null) {
            System.out.println("Cerrando WebDriver actual al finalizar el flujo de tests...");
            try {
                currentDriver.quit();
            } catch (Exception e) {
                System.err.println("Error al cerrar WebDriver: " + e.getMessage());
            } finally {
                TestFailureWatcher.setDriver(null);
                currentDriver = null;
            }
            System.out.println("WebDriver cerrado exitosamente.");
        }
    }

    /**
     * Cierra todos los WebDrivers activos. Este método se usa principalmente desde el shutdown hook.
     */
    protected static void closeAllDrivers() {
        // Cerrar el WebDriver actual si existe
        closeCurrentDriver();

        // Cerrar todos los WebDrivers registrados
        if (!activeDrivers.isEmpty()) {
            System.out.println("Cerrando " + activeDrivers.size() + " instancias de WebDriver adicionales...");
            for (Map.Entry<String, WebDriver> entry : activeDrivers.entrySet()) {
                try {
                    WebDriver driverToClose = entry.getValue();
                    if (driverToClose != null) {
                        System.out.println("Cerrando WebDriver con ID: " + entry.getKey());
                        driverToClose.quit();
                    }
                } catch (Exception e) {
                    System.err.println("Error al cerrar WebDriver: " + e.getMessage());
                }
            }
            activeDrivers.clear();
        }
    }

    /**
     * Cierra manualmente el WebDriver actual.
     * Útil para finalizar explícitamente un flujo de tests.
     */
    protected void forceCloseDriver() {
        if (driver != null) {
            System.out.println("Forzando cierre del WebDriver...");
            try {
                // Cerrar todas las ventanas/pestañas abiertas primero
                if (tabManager != null) {
                    try {
                        tabManager.closeAllTabs();
                    } catch (Exception e) {
                        System.err.println("Error al cerrar pestañas: " + e.getMessage());
                    }
                }

                driver.quit();
            } catch (Exception e) {
                System.err.println("Error al cerrar WebDriver: " + e.getMessage());
            } finally {
                // Limpiar todas las referencias
                if (driverId != null) {
                    activeDrivers.remove(driverId);
                }

                driver = null;

                // Si este era el driver actual, limpiar también esa referencia
                if (driver == currentDriver) {
                    currentDriver = null;
                    TestFailureWatcher.setDriver(null);
                }
            }

            // Limpiar el directorio temporal
            cleanTempDirectory();
        }
    }

    /**
     * Realiza el login usando la estrategia actual.
     * Detecta si ya hay una sesión activa para evitar logins innecesarios.
     *
     * @return true si el login fue exitoso, false en caso contrario
     */
    protected boolean performLogin() {
        // Verificar primero que el driver esté activo
        if (!isDriverActive(driver)) {
            System.err.println("WebDriver no está activo. Creando uno nuevo...");
            setupChromeDriver();
            TestFailureWatcher.setDriver(driver);
            setupLoginStrategy();
        }

        // Si ya existe una sesión activa del tipo correcto, no hacer login de nuevo
        try {
            if (loginStrategy.isSessionActive(driver)) {
                System.out.println("Sesión activa detectada para " + loginStrategy.getStrategyName() + ". No se requiere nuevo login.");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error al verificar sesión: " + e.getMessage());
        }

        boolean loginSuccess = loginStrategy.performLogin(driver);

        if (!loginSuccess || !loginStrategy.isSessionActive(driver)) {
            System.err.println("Error en login: No se estableció sesión correctamente.");
            return false;
        }

        System.out.println("Login exitoso usando " + loginStrategy.getStrategyName() + ". Sesión activa.");
        return true;
    }

    /**
     * Limpieza que se ejecuta después de cada test.
     * Cierra el WebDriver si no estamos en modo de flujo continuo.
     */
    @AfterEach
    public void tearDown() {
        // Si estamos en un flujo continuo, no cerramos el WebDriver entre tests
        if (continuousFlow && isDriverActive(driver)) {
            System.out.println("Omitiendo cierre de WebDriver debido a flujo continuo de tests.");

            // Verificar si hay pestañas huérfanas y cerrarlas
            if (tabManager != null) {
                try {
                    tabManager.closeOrphanedTabs();
                } catch (Exception e) {
                    System.err.println("Error al cerrar pestañas huérfanas: " + e.getMessage());
                }
            }

            return;
        }

        if (driver != null) {
            System.out.println("Cerrando navegador después del test...");
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error al cerrar WebDriver: " + e.getMessage());
            } finally {
                if (driverId != null) {
                    activeDrivers.remove(driverId);
                }

                driver = null;

                // Si este era el driver actual, limpiar también esa referencia
                if (driver == currentDriver) {
                    currentDriver = null;
                    TestFailureWatcher.setDriver(null);
                }
            }
        }

        // Limpiar el directorio temporal
        cleanTempDirectory();
    }

    /**
     * Limpia el directorio temporal creado para el perfil de Chrome.
     */
    private void cleanTempDirectory() {
        // Limpiar el directorio temporal
        if (tempUserDataDir != null && tempUserDataDir.exists()) {
            try {
                deleteDirectory(tempUserDataDir);
                System.out.println("Directorio temporal eliminado: " + tempUserDataDir.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error al eliminar directorio temporal: " + e.getMessage());
            }
        }
    }

    /**
     * Método recursivo para eliminar directorios.
     *
     * @param directory directorio a eliminar
     */
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