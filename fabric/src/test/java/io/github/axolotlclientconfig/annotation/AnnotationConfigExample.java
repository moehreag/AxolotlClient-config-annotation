package io.github.axolotlclientconfig.annotation;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.Config;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused")
public class AnnotationConfigExample implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AxolotlClientAnnotationConfigManager.registerConfig(ExampleConfigClass.class);
    }

    @Config(name = "axolotlclient-annotationconfig-test")
    public static class ExampleConfigClass {
        public boolean exampleBoolean = true;
        public Color someColor = Color.SELECTOR_BLUE.withAlpha(255);
        public double someDouble = 0.25d;

        public String exampleString = "1111122223333";
    }
}
