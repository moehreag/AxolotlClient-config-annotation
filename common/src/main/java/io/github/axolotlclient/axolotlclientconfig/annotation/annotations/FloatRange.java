package io.github.axolotlclient.axolotlclientconfig.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An optional annotation for declaring additional values for Floats.
 * If it is not present defaults to 0 as the minimum value and 10 as the maximum value.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FloatRange {

	/**
	 * A minimal value for your float. Will be used for the slider and clamping the value.
	 * @return the minimal value
	 */
	float min() default 0;

	/**
	 * A maximal value for your float. Will be used for the slider and clamping the value.
	 * @return the maximal value
	 */
	float max() default 10;
}
