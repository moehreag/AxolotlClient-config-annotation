package io.github.axolotlclient.config.annotation.example;

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

	public String exampleString = "Example String";
	public boolean exampleBoolean = false;
	public Color exampleColor = Color.parse("#00FFaa");
	public int exampleInt = 5;
	public Integer exampleInteger = 2;
	@IntRange(max = 26)
	public Integer exampleIntegerRange = 13;

	@Listener("floatListener")
	public float exampleFloatListener = 0.23F;

	public void floatListener(float value){
		System.out.println("Value changed to: "+value);
	}

	public ExampleAnnotationConfigEnum exampleEnum = ExampleAnnotationConfigEnum.EXAMPLE_ENUM1;

	public exampleClass exampleClass = new exampleClass();

	public static class exampleClass {
		public String exampleString = "Example String";
		@Listener("exampleMethod")
		public boolean exampleBooleanListener = false;
		public Color exampleColor = Color.parse("#FFaa00");
		public int exampleInt = 8;
		public Integer exampleInteger = 4;
		@IntRange(max = 45, min = 6)
		public Integer exampleIntegerRange = 32;

		public void exampleMethod(boolean value){
			System.out.println("Value changed to: "+value);
			System.out.println("Value of the field: "+exampleBooleanListener);
		}
	}
}
