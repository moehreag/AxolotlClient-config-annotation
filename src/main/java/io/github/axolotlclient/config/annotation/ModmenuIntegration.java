package io.github.axolotlclient.config.annotation;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import net.minecraft.client.MinecraftClient;

public class ModmenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (parent) -> {
			AxolotlClientConfigManager.openConfigScreen(ExampleAnnotationConfigInitializer.modid);
			return MinecraftClient.getInstance().currentScreen;
		};
	}
}
