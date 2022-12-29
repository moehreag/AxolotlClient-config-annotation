package io.github.axolotlclient.config.annotation;

import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.ConfigHolder;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.config.annotation.annotations.*;
import org.quiltmc.loader.api.QuiltLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The main class for registering a simple, annotation-based config class.
 */

public class AxolotlClientAnnotationConfigManager extends AxolotlClientConfigManager {

	private static final Set<Object> intializedConfigs = new HashSet<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(AxolotlClientAnnotationConfigManager.class);

	/**
	 * Register a config class with Annotation support
	 * @param config the config class Class
     * @param <C> The config class
	 * @return the name used to handle config operations with {@link AxolotlClientConfigManager} for this config.
	 * Should be your mod's modid for automatic modmenu integration
	 */

	public static <C> ConfigInstance<C> registerConfig(Class<C> config){

		String name;
		C conf = null;

		if(config.isAnnotationPresent(Config.class)){
			name = config.getAnnotation(Config.class).name().isEmpty() ?
					config.getSimpleName() : config.getAnnotation(Config.class).name();
		} else name = config.getSimpleName();

		try {
			conf = config.getConstructor().newInstance();
		} catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			LOGGER.error("Couldn't create new Instance of config class for mod "+name+"!");
		}

		if(!QuiltLoader.isModLoaded(name) && QuiltLoader.isDevelopmentEnvironment()){
			LOGGER.debug("(Annotation Addon / Debug) Config "+name+" does not have a mod with the same id. Automatic Modmenu integration will not work.\n" +
					"This message will not be shown in a production environment.");
		}

		OptionCategory category = generateCategory(name, config, conf, conf);
		AxolotlClientConfigManager.registerConfig(name, new AnnotationConfigHolder(category));

		intializedConfigs.add(conf);

		return new ConfigInstance<>(name, conf);
	}

	private static OptionCategory generateCategory(String name, Class<?> clazz, Object conf, Object declaringClass){
		OptionCategory category = new OptionCategory(name);

		for (Field f : clazz.getDeclaredFields()) {
			try {
				if (f.getType().getEnclosingClass() != null && f.getType().getEnclosingClass().equals(declaringClass.getClass()) && !f.getType().isEnum()){
					category.add(generateCategory(f.getName(), f.getType(), conf, f.get(declaringClass)));
				} else if (f.getDeclaringClass().equals(clazz)) {
					Option<?> o = getOption(f, declaringClass, conf);
					if (o != null) {
						category.add(o);
					} else {
						LOGGER.warn("Couldn't do anything with field "+f.getName()+" of type "+f.getType());
					}
				} else {
					LOGGER.warn("Couldn't do anything with field "+f.getName());
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		return category;
	}

	private static Option<?> getOption(Field field, Object clazz, Object configObject){
		try {
			field.setAccessible(true);
			Object val = field.get(clazz);

			if (val instanceof Boolean) {
				return new BooleanOption(field.getName(), value -> setField(field, value, clazz, configObject), (Boolean) val);
			} else if (val instanceof String) {
				return new StringOption(field.getName(), value -> setField(field, value, clazz, configObject), (String) val);
			} else if (val instanceof Integer) {
				if(field.isAnnotationPresent(IntRange.class)){
					return new IntegerOption(field.getName(), value -> setField(field, value, clazz, configObject), (Integer) val, field.getAnnotation(IntRange.class).min(), field.getAnnotation(IntRange.class).max());
				}
				return new IntegerOption(field.getName(), value -> setField(field, value, clazz, configObject), (Integer) val, getSliderDefaultMin(), getSliderDefaultMax());
			} else if (val instanceof Float) {
				if(field.isAnnotationPresent(FloatRange.class)){
					return new FloatOption(field.getName(), value -> setField(field, value, clazz, configObject), (Float) val, field.getAnnotation(FloatRange.class).min(), field.getAnnotation(FloatRange.class).max());
				}
				return new FloatOption(field.getName(), value -> setField(field, value, clazz, configObject), (Float) val, (float) getSliderDefaultMin(), (float) getSliderDefaultMax());
			} else if (val instanceof Double) {
				if (field.isAnnotationPresent(DoubleRange.class)) {
					return new DoubleOption(field.getName(), value -> setField(field, value, clazz, configObject), (Double) val, field.getAnnotation(DoubleRange.class).min(), field.getAnnotation(DoubleRange.class).max());
				}
				return new DoubleOption(field.getName(), value -> setField(field, value, clazz, configObject), (Double) val, getSliderDefaultMin(), getSliderDefaultMax());
			} else if (val instanceof Color) {
				return new ColorOption(field.getName(), value -> setField(field, value, clazz, configObject), (Color) val);
			} else if (val.getClass().isEnum()) {
				return new EnumOption(field.getName(), value -> {
					try {
						setField(field,
								(val.getClass().getMethod("valueOf", String.class).invoke(clazz, value)), clazz, configObject);
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						LOGGER.error("This should not have happened, how did you create an enum without accessible 'valueOf' Method?");
					}
				},
						Arrays.stream(val.getClass().getEnumConstants()).toArray(), val.toString());
			}
		} catch (Exception e){
			LOGGER.warn("Unsupported field in config class "+clazz.getClass().getName()+": "+field.getName());
		}
		return null;
	}

	private static <T> void setField(Field field, T value, Object declaringClass, Object configObject) {
		if (intializedConfigs.contains(configObject)) {
			try {
				field.set(declaringClass, value);
				if (field.isAnnotationPresent(Listener.class)) {
					try {
						field.getDeclaringClass().getDeclaredMethod(field.getAnnotation(Listener.class).value(), field.getType()).invoke(declaringClass, value);
					} catch (ReflectiveOperationException e){
						LOGGER.error("Listener Method '"+field.getAnnotation(Listener.class).value()+"' could not be found or has the wrong parameters!");
					}
				}
				if(!field.get(declaringClass).equals(value)){
					LOGGER.error("Field "+field.getName()+" could not be set to its new value!");
				}
			} catch (IllegalAccessException e) {
				LOGGER.error("Field "+field.getName()+" could not be set to its new value!");
			}
		}
	}

	private static int getSliderDefaultMin(){
		return 0;
	}

	private static int getSliderDefaultMax(){
		return 10;
	}

	private static class AnnotationConfigHolder extends ConfigHolder {

		private final OptionCategory config;

		private AnnotationConfigHolder(OptionCategory config){
			this.config = config;
		}

		@Override
		public List<OptionCategory> getCategories() {
			return Collections.singletonList(config);
		}
	}
}
