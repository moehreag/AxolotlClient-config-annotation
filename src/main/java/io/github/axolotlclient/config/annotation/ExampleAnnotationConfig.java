package io.github.axolotlclient.config.annotation;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.config.annotation.annotations.Config;
import io.github.axolotlclient.config.annotation.annotations.IntRange;

@SuppressWarnings("unused")
@Config(name = "axolotlclient-annotationconfig-example")
public class ExampleAnnotationConfig {

	public static String exampleString = "Example String";
	public static boolean exampleBoolean = false;
	public static Color exampleColor = Color.parse("#00FFaa");
	public static int exampleInt = 5;
	public static Integer exampleInteger = 2;
	@IntRange(max = 26)
	public static Integer exampleIntegerRange = 13;

	public enum exampleEnum {
		EXAMPLE_ENUM1,
		EXAMPLE_ENUM2,
		EXAMPLE_ENUM3
	}

	public static class exampleClass {
		public static String exampleString = "Example String";
		public static boolean exampleBoolean = false;
		public static Color exampleColor = Color.parse("#FFaa00");
		public static int exampleInt = 8;
		public static Integer exampleInteger = 4;
		@IntRange(max = 45, min = 6)
		public static Integer exampleIntegerRange = 32;
	}
}
