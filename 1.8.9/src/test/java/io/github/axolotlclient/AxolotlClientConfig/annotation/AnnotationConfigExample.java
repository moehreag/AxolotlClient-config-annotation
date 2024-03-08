package io.github.axolotlclient.AxolotlClientConfig.annotation;

import java.util.Arrays;

import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.Config;
import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.FloatRange;
import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.SerializedName;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.GraphicsImpl;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused")
public class AnnotationConfigExample implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigInstance<ExampleConfigClass> c = AxolotlClientAnnotationConfig.getInstance().registerConfig(ExampleConfigClass.class);
        System.out.println("Fields after registering config: ");
        System.out.println(c.getConfig());

    }

    @Config(name = "axolotlclient-annotationconfig-test")

    public static class ExampleConfigClass {
        public boolean exampleBoolean = true;
        public Color someColor = Colors.TURQUOISE.withAlpha(255);
        public double someDouble = 0.25d;

        public String exampleString = "1111122223333";
        public int someInt = 2;

        @FloatRange(min = 13, max = 50)
        public float someFloat = 24;

        @SerializedName("some_graphics")
        public Graphics exampleGraphics = new GraphicsImpl(new int[17][17]);

        @Override
        public String toString() {
			return "ExampleConfigClass{" + "exampleBoolean=" + exampleBoolean +
				   ", someColor=" + someColor +
				   ", someDouble=" + someDouble +
				   ", exampleString='" + exampleString + '\'' +
				   ", someInt=" + someInt +
				   ", someFloat=" + someFloat +
				   ", exampleGraphics=" + Arrays.toString(exampleGraphics.getPixelData()) +
				   '}';
        }
    }
}
