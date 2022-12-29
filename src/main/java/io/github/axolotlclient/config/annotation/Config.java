package io.github.axolotlclient.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Config annotation for additional info about your config
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {

	/**
	 * An optional name to use for the config file + internal handling of the config
	 * @return the name of the configuration
	 */
	String name() default "";
}
