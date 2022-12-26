package io.github.axolotlclient.config.annotation;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.config.annotation.annotations.Config;
import io.github.axolotlclient.config.annotation.annotations.IntRange;
import io.github.axolotlclient.config.annotation.annotations.Listener;

/**
 * An example config class showing how to use this addition to the library.
 */

@SuppressWarnings("unused")
@Config(name = "axolotlclient-annotationconfig")
public class ExampleAnnotationConfig {

	public static String exampleString = "Example String";
	public static boolean exampleBoolean = false;
	public static Color exampleColor = Color.parse("#00FFaa");
	public static int exampleInt = 5;
	public static Integer exampleInteger = 2;
	@IntRange(max = 26)
	public static Integer exampleIntegerRange = 13;

	@Listener("floatListener")
	public static float exampleFloatListener = 0.23F;

	public static void floatListener(){
		System.out.println("Value changed to: "+exampleFloatListener);
	}

	public enum exampleEnum {
		EXAMPLE_ENUM1,
		EXAMPLE_ENUM2,
		EXAMPLE_ENUM3
	}

	public static class exampleClass {
		public static String exampleString = "Example String";
		@Listener("exampleMethod")
		public static boolean exampleBooleanListener = false;
		public static Color exampleColor = Color.parse("#FFaa00");
		public static int exampleInt = 8;
		public static Integer exampleInteger = 4;
		@IntRange(max = 45, min = 6)
		public static Integer exampleIntegerRange = 32;

		public static void exampleMethod(){
			System.out.println("Value changed to: "+exampleBoolean);
		}
	}
}
