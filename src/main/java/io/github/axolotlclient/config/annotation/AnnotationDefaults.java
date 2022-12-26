package io.github.axolotlclient.config.annotation;

/**
 * This class provides default values for sliders which
 * require a minimal and a maximal value on top of the actual default value.
 */
public class AnnotationDefaults {
	
	public static int getMin(){
		return 0;
	}
	
	public static int getMax(){
		return 10;
	}
	
}
