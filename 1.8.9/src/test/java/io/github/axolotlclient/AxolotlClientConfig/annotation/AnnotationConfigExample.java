package io.github.axolotlclient.AxolotlClientConfig.annotation;

import java.util.Arrays;

import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.Config;
import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.FloatRange;
import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.Listener;
import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.SerializedName;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.GraphicsImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings("unused")
public class AnnotationConfigExample implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigInstance<ExampleConfigClass> c = AxolotlClientAnnotationConfig.getInstance().registerConfig(ExampleConfigClass.class, ExampleConfigManager::new);
        System.out.println("Fields after registering config: ");
        System.out.println(c.config());

        if (AxolotlClientConfig.getInstance().getConfigManager(c.id()) instanceof ExampleConfigManager m) {
            System.out.println(m.getSerializedJson());
        }
    }

    // Only used to print out the serialized values, usually not necessary in actual use
    private static class ExampleConfigManager extends JsonConfigManager {

        public ExampleConfigManager(OptionCategory category) {
            super(FabricLoader.getInstance().getConfigDir().resolve(category.getName() + ".json"), category);
        }

        public String getSerializedJson() {
            JsonObject o = new JsonObject();
            save(o, root);
            return o.toString();
        }
    }

    @Config(name = "axolotlclient-annotationconfig-test")
    @SerializedName.RenameAll(NamingScheme.SNAKE_CASE)
    public static class ExampleConfigClass {
        public boolean exampleBoolean = true;
        public Color someColor = Colors.TURQUOISE.withAlpha(255);
        public double someDouble = 0.25d;

        public String exampleString = "1111122223333";
        public int someInt = 2;

        @FloatRange(min = 13, max = 50)
        public float someFloat = 24;

        @SerializedName("some_graphics")
        public Graphics exampleGraphics = new GraphicsImpl(new int[17][17]);

        @Listener("onBooleanTwoChange")
        public boolean booleanTwo = false;

        @Override
        public String toString() {
			return "ExampleConfigClass{" + "exampleBoolean=" + exampleBoolean +
				   ", someColor=" + someColor +
				   ", someDouble=" + someDouble +
				   ", exampleString='" + exampleString + '\'' +
				   ", someInt=" + someInt +
				   ", someFloat=" + someFloat +
				   ", exampleGraphics=" + Arrays.toString(exampleGraphics.getPixelData()) +
				   '}';
        }

        void onBooleanTwoChange(boolean value) {
            System.out.println("Woah you changed this value to "+value+"!");
            System.out.println("Config values: ");
            System.out.println(((ExampleConfigManager)AxolotlClientConfig.getInstance().getConfigManager("axolotlclient-annotationconfig-test")).getSerializedJson());
        }
    }
}
