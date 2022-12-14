package io.github.axolotlclient.config.annotation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntRange {
	int min() default 0;
	int max() default 10;
}
