package io.github.axolotlclient.config.annotation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FloatRange {
	float min() default 0;
	float max() default 10;
}
