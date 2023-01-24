package io.github.axolotlclient.AxolotlClientConfig.annotation;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class AnnotationConfigExampleModmenuIntegration implements ModMenuApi {

    @Override
    public String getModId() {
        return "axolotlclient-annotationconfig-test";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> AxolotlClientAnnotationConfigManager.getInstance().getConfigScreen("axolotlclient-annotationconfig-test", screen);
    }
}
