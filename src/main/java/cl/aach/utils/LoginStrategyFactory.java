package cl.aach.utils;

import cl.aach.models.TestData;

/**
 * Fábrica para obtener la estrategia de login adecuada según el tipo de test.
 * Esta clase implementa el patrón Factory para crear instancias de LoginStrategy.
 */
public class LoginStrategyFactory {

    /**
     * Tipos de login soportados por el sistema.
     */
    public enum LoginType {
        PORTAL,         // Login para el portal principal (default)
        PORTAL_ADMIN,   // Login para el portal con credenciales de administrador
        EMANAGER,       // Login para el sistema eManager
        SGU,            // Login para el sistema SGU
        SGU_ADMIN,      // Login para el sistema SGU con credenciales de administrador
        EMANAGER360     // Login para el sistema eManager 360
    }

    /**
     * Crea una estrategia de login según el tipo solicitado.
     *
     * @param type     tipo de login requerido
     * @param testData datos de prueba con credenciales necesarias
     * @return la estrategia de login correspondiente
     */
    public static LoginStrategy createLoginStrategy(LoginType type, TestData testData) {
        System.out.println("Creando estrategia de login de tipo: " + type);
        switch (type) {
            case PORTAL_ADMIN:
                return new PortalAdminLoginStrategy(testData);
            case EMANAGER:
                return new EmanagerLoginStrategy(testData);
            case EMANAGER360:
                return new EmanagerLoginStrategy(testData); // Usamos la misma implementación por ahora
            case SGU:
                return new SGULoginStrategy(testData);
            case SGU_ADMIN:
                return new SGUAdminLoginStrategy(testData);
            case PORTAL:
            default:
                return new PortalLoginStrategy(testData);
        }
    }

    /**
     * Detecta automáticamente la estrategia de login a usar basándose en el nombre de la clase de test.
     * Este método usa convenciones de nombres para determinar qué estrategia utilizar.
     *
     * @param testClassName nombre completo de la clase de test
     * @param testData datos de prueba con credenciales
     * @return la estrategia de login adecuada según el nombre de la clase
     */
    public static LoginStrategy detectLoginStrategy(String testClassName, TestData testData) {
        System.out.println("Detectando estrategia para clase: " + testClassName);

        LoginType detectedType = LoginType.PORTAL; // Valor por defecto

        if (testClassName.toLowerCase().contains("emanager")) {
            if (testClassName.contains("360")) {
                detectedType = LoginType.EMANAGER360;
            } else {
                detectedType = LoginType.EMANAGER;
            }
        } else if (testClassName.toLowerCase().contains("sgu")) {
            if (testClassName.toLowerCase().contains("admin")) {
                detectedType = LoginType.SGU_ADMIN;
            } else {
                detectedType = LoginType.SGU;
            }
        } else if (testClassName.toLowerCase().contains("admin")) {
            detectedType = LoginType.PORTAL_ADMIN;
        }

        System.out.println("Estrategia detectada: " + detectedType);
        return createLoginStrategy(detectedType, testData);
    }

    /**
     * Obtiene la estrategia de login para un test específico.
     * Este método puede ser utilizado para forzar una estrategia específica cuando la detección automática no es suficiente.
     *
     * @param testClass clase de test para la que se requiere la estrategia
     * @param testData datos de prueba con credenciales
     * @param defaultType tipo de login a usar si no hay anotación específica
     * @return la estrategia de login adecuada
     */
    public static LoginStrategy getStrategyForTest(Class<?> testClass, TestData testData, LoginType defaultType) {
        // Verificar si hay una anotación explícita
        if (testClass.isAnnotationPresent(cl.aach.annotations.UseLoginStrategy.class)) {
            cl.aach.annotations.UseLoginStrategy annotation = testClass.getAnnotation(cl.aach.annotations.UseLoginStrategy.class);
            return createLoginStrategy(annotation.value(), testData);
        }

        // Si no hay anotación, usar el tipo por defecto especificado
        if (defaultType != null) {
            return createLoginStrategy(defaultType, testData);
        }

        // Si no se especifica tipo por defecto, detectar automáticamente
        return detectLoginStrategy(testClass.getName(), testData);
    }
}