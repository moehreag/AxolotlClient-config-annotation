package com.example.annotation;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;

/*
 * Example of setting up a config screen manually. In the case of modmenu
 * this shouldn't be necessary if your config uses your mod's id as the name.
 * (Therefore this is unused in this example)
 */
public class AnnotationConfigExampleModmenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigManager manager = AxolotlClientConfig.getInstance().getConfigManager("axolotlclient-annotationconfig-example");
            return ConfigUI.getInstance().getScreen(this.getClass().getClassLoader(),
                    manager.getRoot(), parent);
        };
    }
}
