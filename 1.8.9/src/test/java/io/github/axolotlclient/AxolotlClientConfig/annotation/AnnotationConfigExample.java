package io.github.axolotlclient.AxolotlClientConfig.annotation;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.*;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class AnnotationConfigExample implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigInstance<ExampleConfigClass> c = AxolotlClientAnnotationConfig.getInstance().registerConfig(ExampleConfigClass.class);
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

        public int[][] exampleGraphics = new int[17][17];
    }
}
