package cl.aach.annotations;

import cl.aach.utils.LoginStrategyFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para especificar manualmente el tipo de estrategia de login a usar.
 * Si no se especifica, se detectará automáticamente basándose en el nombre de la clase.
 *
 * Ejemplo de uso:
 *
 * @UseLoginStrategy(LoginStrategyFactory.LoginType.PORTAL_ADMIN)
 * public class MiTest extends BaseTest { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseLoginStrategy {
    /**
     * El tipo de estrategia de login a utilizar. Por defecto es PORTAL.
     */
    LoginStrategyFactory.LoginType value() default LoginStrategyFactory.LoginType.PORTAL;
}