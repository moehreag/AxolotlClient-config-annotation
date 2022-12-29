package io.github.axolotlclient.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An optional annotation for declaring additional values for Integers.
 * If it is not present defaults to 0 as the minimum value and 10 as the maximum value.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IntRange {

	/**
	 * A minimal value for your Integer. Will be used for the slider and clamping the value.
	 * @return the minimal value
	 */
	int min() default 0;

	/**
	 * A maximal value for your Integer. Will be used for the slider and clamping the value.
	 * @return the maximal value
	 */
	int max() default 10;
}
