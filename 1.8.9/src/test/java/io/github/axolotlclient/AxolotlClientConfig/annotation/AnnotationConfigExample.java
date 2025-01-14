package io.github.axolotlclient.AxolotlClientConfig.annotation;

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
        if (AxolotlClientConfig.getInstance().getConfigManager(c.id()) instanceof ExampleConfigManager m) {
            System.out.println(m.getSerializedJson());
        }

        // Access the config fields through the object provided by the ConfigInstance
        System.out.println(c.config().exampleBoolean);
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

        public SubCategory c2 = new SubCategory();
        public SubCategory c3 = new SubCategory();
        public SubCategory c4 = new SubCategory();
        public SubCategory c5 = new SubCategory();
        @SuppressWarnings("NonAsciiCharacters")
        public SubCategory VeryBADLY_namedüFIELD = new SubCategory();
        public SubCategory c7 = new SubCategory();
        public SubCategory c8 = new SubCategory();
        public SubCategory c9 = new SubCategory();
        public SubCategory SCREAMING_SNAKE_CASE_NAMED_FIELD = new SubCategory();
        public SubCategory PascalCaseNamedField = new SubCategory();

        @SerializedName.RenameAll(NamingScheme.KEBAB_CASE)
        public static class SubCategory {
            public boolean subBoolean = true;
            public Color veryLengthyColorOption______________ = Colors.DARK_YELLOW;
        }

        void onBooleanTwoChange(boolean value) {
            System.out.println("Woah you changed this value to "+value+"!");
            System.out.println("Config values: ");
            System.out.println(((ExampleConfigManager)AxolotlClientConfig.getInstance().getConfigManager("axolotlclient-annotationconfig-test")).getSerializedJson());
        }
    }
}
