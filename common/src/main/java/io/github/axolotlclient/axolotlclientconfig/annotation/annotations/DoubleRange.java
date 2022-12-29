package io.github.axolotlclient.axolotlclientconfig.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An optional annotation for declaring additional values for Doubles.
 * If it is not present defaults to 0 as the minimum value and 10 as the maximum value.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DoubleRange {

	/**
	 * A minimal value for your double. Will be used for the slider and clamping the value.
	 * @return the minimal value
	 */
	double min() default 0;

	/**
	 * A maximal value for your double. Will be used for the slider and clamping the value.
	 * @return the maximal value
	 */
	double max() default 10;
}
