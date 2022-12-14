package io.github.axolotlclient.config.annotation;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class ExampleAnnotationConfigInitializer implements ClientModInitializer {

	public static String modid = "axolotlclient-annotationconfig-example";

	@Override
	public void onInitializeClient(ModContainer mod) {
		AxolotlClientAnnotationConfigManager.registerConfig(ExampleAnnotationConfig.class);
	}
}
