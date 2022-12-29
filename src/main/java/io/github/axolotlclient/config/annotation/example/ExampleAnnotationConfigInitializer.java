package io.github.axolotlclient.config.annotation.example;

import io.github.axolotlclient.config.annotation.AxolotlClientAnnotationConfigManager;
import io.github.axolotlclient.config.annotation.ConfigInstance;
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
			ConfigInstance<ExampleAnnotationConfig> instance = AxolotlClientAnnotationConfigManager.registerConfig(ExampleAnnotationConfig.class);
			configName = instance.getId();
            ExampleAnnotationConfig config = instance.getConfig(); // get the instance of the config
		}
	}
}
