package io.github.axolotlclientconfig.annotation;

import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.Config;
import io.github.axolotlclient.config.AxolotlClientAnnotationConfigManager;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class AnnotationConfigExample implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        AxolotlClientAnnotationConfigManager.registerConfig(ExampleConfigClass.class);
    }

    @Config(name = "axolotlclient-annotationconfig-test")
    public static class ExampleConfigClass {
        public boolean exampleBoolean = true;
    }
}
