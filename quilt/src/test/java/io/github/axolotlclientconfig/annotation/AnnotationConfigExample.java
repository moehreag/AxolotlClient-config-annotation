package io.github.axolotlclientconfig.annotation;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.Config;
import io.github.axolotlclient.config.AxolotlClientAnnotationConfigManager;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

@SuppressWarnings("unused")
public class AnnotationConfigExample implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        AxolotlClientAnnotationConfigManager.registerConfig(ExampleConfigClass.class);
    }

    @Config(name = "axolotlclient-annotationconfig-test")
    public static class ExampleConfigClass {
        public boolean exampleBoolean = true;
        public Color someColor = Color.SELECTOR_BLUE.withAlpha(255);
        public double someDouble = 0.25d;

        public String exampleString = "1111122223333";
        public int someInt = 2;
        public float someFloat = 24;
    }

}
