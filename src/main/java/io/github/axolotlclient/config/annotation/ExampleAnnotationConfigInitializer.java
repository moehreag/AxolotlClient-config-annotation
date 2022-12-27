package io.github.axolotlclient.config.annotation;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

/**
 * The initializer for the config example provided with this mod.
 */

public class ExampleAnnotationConfigInitializer implements ClientModInitializer {

	@ApiStatus.Internal
	static String configName = "axolotlclient-annotationconfig";

	@Override
	public void onInitializeClient(ModContainer mod) {
		if(QuiltLoader.isDevelopmentEnvironment()) {
			configName = AxolotlClientAnnotationConfigManager.registerConfig(ExampleAnnotationConfig.class);
		}
	}
}
