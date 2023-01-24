package io.github.axolotlclient.AxolotlClientConfig.annotation;

import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.options.KeyBindOption;
import io.github.axolotlclient.AxolotlClientConfig.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.axolotlclientconfig.annotation.AnnotationConfigException;
import io.github.axolotlclient.axolotlclientconfig.annotation.AnnotationConfigManager;
import io.github.axolotlclient.axolotlclientconfig.annotation.ConfigInstance;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.Config;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.Listener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.options.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * The main class for registering a simple, annotation-based config class.
 */

public final class AxolotlClientAnnotationConfigManager extends AxolotlClientConfigManager implements AnnotationConfigManager {

    private static final AxolotlClientAnnotationConfigManager Instance = new AxolotlClientAnnotationConfigManager();
    private static final Set<Object> intializedConfigs = new HashSet<>();
    private static final Logger LOGGER = LogManager.getLogger(AxolotlClientAnnotationConfigManager.class);

    private AxolotlClientAnnotationConfigManager() {
    }

    /**
     * Register a config class with Annotation support.
     * Changes in the values of the fields will <b>not</b> be represented in the config screen or the config file.
     * However, the fields will represent the current values of the options.
     *
     * @param config the config class Class
     * @param <C>    The config class
     * @return a io.github.axolotlclient.axolotlclientconfig.annotation.ConfigInstance for this config.
     * Should be your mod's modid for automatic modmenu integration
     */

    public <C> ConfigInstance<C> registerConfig(Class<C> config) {

        String name;
        C conf;

        if (config.isAnnotationPresent(Config.class)) {
            name = config.getAnnotation(Config.class).name().isEmpty() ?
                    config.getSimpleName() : config.getAnnotation(Config.class).name();
        } else name = config.getSimpleName();

        try {
            conf = config.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            LOGGER.error("Couldn't create new Instance of config class for mod " + name + "!");
            throw new AnnotationConfigException("Config class must be public and have a no-args constructor!");
        }

        if (!FabricLoader.getInstance().isModLoaded(name) && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.info("(Annotation Addon / Debug) Config " + name + " does not have a mod with the same id. Automatic Modmenu integration will not work.\n" +
                    "This message will not be shown in a production environment.");
        }

        OptionCategory category = generateCategory(name, config, conf, conf);
        getInstance().registerConfig(name, new AnnotationConfigManager.AnnotationConfigHolder(category));

        intializedConfigs.add(conf);

        return new ConfigInstance<>(name, conf);
    }

    public static AxolotlClientAnnotationConfigManager getInstance() {
        return Instance;
    }

    public Option<?> getOptionVersioned(Field field, Object clazz, Object configObject, Object val) {
        if (val instanceof KeyBinding) {
            if (!field.isAnnotationPresent(Listener.class)) {
                LOGGER.info("Keybind without Listener annotation found! It will have no function!");
            }
            return new KeyBindOption(field.getName(), (KeyBinding) val, keyBind -> {
                try {
                    field.getDeclaringClass().getDeclaredMethod(field.getAnnotation(Listener.class).value(), field.getType()).invoke(clazz, keyBind);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new AnnotationConfigException("KeyBind listener invocation failed! ", e);
                }
            });
        }
        return null;
    }

    @Override
    public boolean isRegistered(Object o) {
        return intializedConfigs.contains(o);
    }

    @Override
    public void error(String msg) {
        LOGGER.error(msg);
    }

    @Override
    public void warn(String msg) {
        LOGGER.warn(msg);
    }

    @Override
    public void info(String msg) {
        LOGGER.info(msg);
    }
}
