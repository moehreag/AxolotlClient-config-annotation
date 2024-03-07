package io.github.axolotlclient.AxolotlClientConfig.annotation;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;

public class AnnotationConfigExampleModmenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigManager manager = AxolotlClientConfig.getInstance().getConfigManager("axolotlclient-annotationconfig-test");
            return ConfigUI.getInstance().getScreen(this.getClass().getClassLoader(),
                    manager.getRoot(), parent);
        };
    }
}
