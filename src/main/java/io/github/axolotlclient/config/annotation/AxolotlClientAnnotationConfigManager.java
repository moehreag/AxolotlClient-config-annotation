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
import java.util.*;

/**
 * The main class for registering a simple, annotation-based config class.
 */

public class AxolotlClientAnnotationConfigManager {

	private static final Map<Class<?>, OptionCategory> annotationconfigs = new HashMap<>();
	private static final Set<Class<?>> intializedConfigs = new HashSet<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(AxolotlClientAnnotationConfigManager.class);

	/**
	 * Register a config class with Annotation support
	 * @param config the config class
	 * @return the name used to handle config operations with {@link AxolotlClientConfigManager} for this config.
	 * Should be your mod's modid for automatic modmenu integration
	 */

	public static String registerConfig(Class<?> config){

		String name;

		if(config.isAnnotationPresent(Config.class)){
			name = config.getAnnotation(Config.class).name().isEmpty() ?
					config.getSimpleName() : config.getAnnotation(Config.class).name();
		} else name = config.getSimpleName();

		if(!QuiltLoader.isModLoaded(name) && QuiltLoader.isDevelopmentEnvironment()){
			LOGGER.debug("(Annotation Addon / Debug) Config "+name+" does not have a mod with the same id. Automatic Modmenu integration will not work.\n" +
					"This message will not be shown in a production environment.");
		}

		OptionCategory category = generateCategory(name, config, config);
		annotationconfigs.put(config, category);
		AxolotlClientConfigManager.registerConfig(name, new ConfigHolder() {
			@Override
			public List<OptionCategory> getCategories() {
				return Collections.singletonList(annotationconfigs.get(config));
			}
		});

		intializedConfigs.add(config);
		return name;
	}

	private static OptionCategory generateCategory(String name, Class<?> clazz, Class<?> orig){
		OptionCategory category = new OptionCategory(name);

		for(Field f : clazz.getDeclaredFields()){
			if(f.getDeclaringClass().equals(clazz)) {
				Option<?> o = getOption(f, orig);
				if(o != null){
					category.add(o);
				}
			}
		}

		for (Class<?> c: clazz.getDeclaredClasses()){
			if(c.isEnum()){
				category.add(new EnumOption(c.getSimpleName(), c.getEnumConstants(), null));
			} else {
				category.add(generateCategory(c.getSimpleName(), c, orig));
			}
		}

		return category;
	}

	private static Option<?> getOption(Field field, Class<?> clazz){
		try {
			field.setAccessible(true);

			if (field.getType().getSimpleName().equalsIgnoreCase("boolean")) {
				return new BooleanOption(field.getName(), value -> setField(field, value, clazz), field.getBoolean(null));
			} else if (field.getType().getSimpleName().equalsIgnoreCase("string")){
				return new StringOption(field.getName(), value -> setField(field, value, clazz), (String) field.get(null));
			} else if (field.getType()
					.getSimpleName().toLowerCase(Locale.ROOT).contains("int")){
				if(field.isAnnotationPresent(IntRange.class)){
					return new IntegerOption(field.getName(), value -> setField(field, value, clazz), (Integer) field.get(null), field.getAnnotation(IntRange.class).min(), field.getAnnotation(IntRange.class).max());
				}
				return new IntegerOption(field.getName(), value -> setField(field, value, clazz), (Integer) field.get(null), getSliderDefaultMin(), getSliderDefaultMax());
			} else if (field.getType().getSimpleName().equalsIgnoreCase("float")){
				if(field.isAnnotationPresent(FloatRange.class)){
					return new FloatOption(field.getName(), value -> setField(field, value, clazz), (Float) field.get(null), field.getAnnotation(FloatRange.class).min(), field.getAnnotation(FloatRange.class).max());
				}
				return new FloatOption(field.getName(), value -> setField(field, value, clazz), (Float) field.get(null), (float) getSliderDefaultMin(), (float) getSliderDefaultMax());
			} else if (field.getType().getSimpleName().equalsIgnoreCase("double")){
				if(field.isAnnotationPresent(DoubleRange.class)){
					return new DoubleOption(field.getName(), value -> setField(field, value, clazz), (Double) field.get(null), field.getAnnotation(DoubleRange.class).min(), field.getAnnotation(DoubleRange.class).max());
				}
				return new DoubleOption(field.getName(), value -> setField(field, value, clazz), (Double) field.get(null), getSliderDefaultMin(), getSliderDefaultMax());
			} else if (field.get(null) instanceof Color){
				return new ColorOption(field.getName(), value -> setField(field, value, clazz), (Color) field.get(null));
			}
		} catch (Exception e){
			LOGGER.warn("Unsupported field in config class "+clazz.getName()+": "+field.getName());
			//e.printStackTrace();
		}
		return null;
	}

	private static <T> void setField(Field field, T value, Class<?> configClass) {
		if (intializedConfigs.contains(configClass)) {
			try {
				field.set(null, value);
				if (field.isAnnotationPresent(Listener.class)) {
					try {


						field.getDeclaringClass().getDeclaredMethod(field.getAnnotation(Listener.class).value(), field.getType()).invoke(null, value);
					} catch (ReflectiveOperationException e){
						LOGGER.error("Listener Method "+field.getAnnotation(Listener.class)+" could not be found or has the wrong parameters!");
					}
				}
				if(!field.get(null).equals(value)){
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
}
