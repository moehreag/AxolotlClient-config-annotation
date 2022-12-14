package io.github.axolotlclient.config.annotation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleRange {

	double min() default 0;
	double max() default 10;
}
