package io.github.axolotlclient.AxolotlClientConfig.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import io.github.axolotlclient.AxolotlClientConfig.annotation.annotations.*;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Annotation Config Entrypoint
 */
public class AxolotlClientAnnotationConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Object> INTIALIZED_CONFIGS = new HashSet<>();

    private static final AxolotlClientAnnotationConfig INSTANCE = new AxolotlClientAnnotationConfig();

    /**
     * Get an instance of this class to register your config.
     * @return an instance of this class
     */
    public static AxolotlClientAnnotationConfig getInstance(){
        return INSTANCE;
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
            error("Couldn't create new Instance of config class for mod " + name + "!");
            throw new AnnotationConfigException("Config class must be public and have a no-args constructor!");
        }

        if (!FabricLoader.getInstance().isModLoaded(name) && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            info("(Annotation Addon / Debug) Config " + name + " does not have a mod with the same id. Automatic Modmenu integration will not work.\n" +
                        "This message will not be shown in a production environment.");
        }

        OptionCategory category = generateCategory(name, config, conf, conf);
        ConfigManager manager = new JsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve(category.getName() + ".json"), category);
        AxolotlClientConfig.getInstance().register(manager);
        manager.load();
        manager.save();

        INTIALIZED_CONFIGS.add(conf);

        updateFields(category, conf, config);

        return new ConfigInstance<>(name, conf);
    }

    private OptionCategory generateCategory(String name, Class<?> clazz, Object conf, Object declaringClass){
        OptionCategory category = OptionCategory.create(name);

        for (Field f : clazz.getDeclaredFields()) {
            try {
                if (f.getType().getEnclosingClass() != null && f.getType().getEnclosingClass().equals(declaringClass.getClass()) && !f.getType().isEnum()){
                    category.add(generateCategory(getFieldName(f), f.getType(), conf, f.get(declaringClass)));
                } else if (f.getDeclaringClass().equals(clazz)) {
                    Option<?> o = getOption(f, declaringClass, conf);
                    if (o != null) {
                        category.add(o);
                    } else {
                        warn("Couldn't do anything with field "+f.getName()+" of type "+f.getType());
                    }
                } else {
                    warn("Couldn't do anything with field "+f.getName());
                }
            } catch (IllegalAccessException e) {
                throw new AnnotationConfigException(e);
            }
        }

        return category;
    }

    private Option<?> getOption(Field field, Object clazz, Object configObject){
        try {
            field.setAccessible(true);
            Object val = field.get(clazz);

            if (val instanceof Boolean) {
                return new BooleanOption(getFieldName(field), (Boolean) val, value -> setField(field, value, configObject));
            } else if (val instanceof String) {
                return new StringOption(getFieldName(field), (String) val, value -> setField(field, value, configObject));
            } else if (val instanceof Integer) {
                if(field.isAnnotationPresent(IntRange.class)){
                    return new IntegerOption(getFieldName(field), (Integer) val, value -> setField(field, value, configObject), field.getAnnotation(IntRange.class).min(), field.getAnnotation(IntRange.class).max());
                }
                return new IntegerOption(getFieldName(field), (Integer) val, value -> setField(field, value, configObject), getSliderDefaultMin(), getSliderDefaultMax());
            } else if (val instanceof Float) {
                if(field.isAnnotationPresent(FloatRange.class)){
                    return new FloatOption(getFieldName(field), (Float) val, value -> setField(field, value, configObject), field.getAnnotation(FloatRange.class).min(), field.getAnnotation(FloatRange.class).max());
                }
                return new FloatOption(getFieldName(field), (Float) val, value -> setField(field, value, configObject), (float) getSliderDefaultMin(), (float) getSliderDefaultMax());
            } else if (val instanceof Double) {
                if (field.isAnnotationPresent(DoubleRange.class)) {
                    return new DoubleOption(getFieldName(field), (Double) val, value -> setField(field, value, configObject), field.getAnnotation(DoubleRange.class).min(), field.getAnnotation(DoubleRange.class).max());
                }
                return new DoubleOption(getFieldName(field), (Double) val, value -> setField(field, value, configObject), (double) getSliderDefaultMin(), (double) getSliderDefaultMax());
            } else if (val instanceof Color) {
                return new ColorOption(getFieldName(field), (Color) val, value -> setField(field, value, configObject));
            } else if (val.getClass().isEnum()) {
                //noinspection unchecked
                return getEnumOption(getFieldName(field), (Class<Object>) val.getClass(), val, value -> setField(field, value, configObject));
            } else if (val instanceof Graphics){
                return new GraphicsOption(getFieldName(field), (Graphics) val, value -> setField(field, value, configObject));
            }
            return null;
        } catch (Exception e){
            warn("Unsupported field in config class "+clazz.getClass().getName()+": "+field.getName());
        }
        return null;
    }

    private EnumOption<?> getEnumOption(String name, Class<Object> clazz, Object defaultValue, OptionBase.ChangeListener<Object> changeListener){
        return new EnumOption<>(name, clazz, defaultValue, changeListener);
    }

    private void updateFields(OptionCategory category, Object config, Class<?> configClass){
        category.getOptions().forEach(option -> {
			try {
                Field f = configClass.getDeclaredField(option.getName());
                Object value = option.get();
                setField(f, value, config);
			} catch (NoSuchFieldException e) {
				error("Failed to update "+option.getName()+": "+e);
			}
		});
        category.getSubCategories().forEach(c -> {
            Class<?>[] cls = configClass.getDeclaredClasses();
            for (Class<?> cl : cls){
                if (cl.getSimpleName().equals(c.getName())) {
                    updateFields(c, config, cl);
                }
            }
		});
    }

    private String getFieldName(Field field){
        if(field.isAnnotationPresent(SerializedName.class)){
            String value = field.getAnnotation(SerializedName.class).value();
            if (!value.trim().isEmpty()){
                return value;
            }
        }
        return field.getName();
    }

    private <T> void setField(Field field, T value, Object configObject) {
        if (isRegistered(configObject)) {
            try {
                field.setAccessible(true);
                field.set(configObject, value);
                if (field.isAnnotationPresent(Listener.class)) {
                    try {
                        field.getDeclaringClass().getDeclaredMethod(field.getAnnotation(Listener.class).value(), field.getType()).invoke(configObject, value);
                    } catch (ReflectiveOperationException e){
                        error("Listener Method '"+field.getAnnotation(Listener.class).value()+"' could not be found or has the wrong parameters!");
                    }
                }
                if(!field.get(configObject).equals(value)){
                    error("Field "+field.getName()+" could not be set to its new value!");
                }
            } catch (IllegalAccessException e) {
                error("Field "+field.getName()+" could not be set to its new value!");
            }
        } else {
            error("Not setting field because the config object "+configObject+" is not known!");
        }
    }

	boolean isRegistered(Object o) {
		return INTIALIZED_CONFIGS.contains(o);
	}

	private int getSliderDefaultMin(){
        return 0;
    }

    private int getSliderDefaultMax(){
        return 10;
    }

    private void error(String msg){
        LOGGER.error(msg);
    }

    private void warn(String msg){
        LOGGER.warn(msg);
    }

    private void info(String msg){
        LOGGER.info(msg);
    }
}
