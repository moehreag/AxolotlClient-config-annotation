package io.github.axolotlclient.config.annotation;

import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.ConfigHolder;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.config.annotation.annotations.*;
import io.github.axolotlclient.util.Logger;
import org.quiltmc.loader.api.QuiltLoader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The main class for registering a simple, annotation-based config class.
 */

public class AxolotlClientAnnotationConfigManager {

	private static final Map<Class<?>, OptionCategory> annotationconfigs = new HashMap<>();

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

		if(!QuiltLoader.isModLoaded(name)){
			Logger.debug("(Annotation Addon / Debug) Config "+name+" does not have a mod with the same id. Automatic Modmenu integration will not work.\n" +
					"This message will not be shown in a production environment.");
		}

		annotationconfigs.put(config, generateCategory(name, config));
		AxolotlClientConfigManager.registerConfig(name, new ConfigHolder() {
			@Override
			public List<OptionCategory> getCategories() {
				return Collections.singletonList(annotationconfigs.get(config));
			}
		});

		return name;
	}

	private static OptionCategory generateCategory(String name, Class<?> clazz){
		OptionCategory category = new OptionCategory(name);

		for(Field f : clazz.getDeclaredFields()){
			if(f.getDeclaringClass().equals(clazz)) {
				Option<?> o = getOption(f);
				if(o != null){
					category.add(o);
				}
			}
		}

		for (Class<?> c: clazz.getDeclaredClasses()){
			if(c.isEnum()){
				category.add(new EnumOption(c.getSimpleName(), c.getEnumConstants(), null));
			} else {
				category.add(generateCategory(c.getSimpleName(), c));
			}
		}

		return category;
	}

	private static Option<?> getOption(Field field){
		try {
			System.out.println(field.getType().getSimpleName());
			System.out.println(field.getName());

			if (field.getType().getSimpleName().equalsIgnoreCase("boolean")) {
				return new BooleanOption(field.getName(), value -> setField(field, value), field.getBoolean(null));
			} else if (field.getType().getSimpleName().equalsIgnoreCase("string")){
				return new StringOption(field.getName(), value -> setField(field, value), (String) field.get(null));
			} else if (field.getType()
					.getSimpleName().toLowerCase(Locale.ROOT).contains("int")){
				if(field.isAnnotationPresent(IntRange.class)){
					return new IntegerOption(field.getName(), value -> setField(field, value), (Integer) field.get(null), field.getAnnotation(IntRange.class).min(), field.getAnnotation(IntRange.class).max());
				}
				return new IntegerOption(field.getName(), value -> setField(field, value), (Integer) field.get(null), getSliderDefaultMin(), getSliderDefaultMax());
			} else if (field.getType().getSimpleName().equalsIgnoreCase("float")){
				if(field.isAnnotationPresent(FloatRange.class)){
					return new FloatOption(field.getName(), value -> setField(field, value), (Float) field.get(null), field.getAnnotation(FloatRange.class).min(), field.getAnnotation(FloatRange.class).max());
				}
				return new FloatOption(field.getName(), value -> setField(field, value), (Float) field.get(null), (float) getSliderDefaultMin(), (float) getSliderDefaultMax());
			} else if (field.getType().getSimpleName().equalsIgnoreCase("double")){
				if(field.isAnnotationPresent(DoubleRange.class)){
					return new DoubleOption(field.getName(), value -> setField(field, value), (Double) field.get(null), field.getAnnotation(DoubleRange.class).min(), field.getAnnotation(DoubleRange.class).max());
				}
				return new DoubleOption(field.getName(), value -> setField(field, value), (Double) field.get(null), getSliderDefaultMin(), getSliderDefaultMax());
			} else if (field.get(null) instanceof Color){
				return new ColorOption(field.getName(), value -> setField(field, value), (Color) field.get(null));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static <T> void setField(Field field, T value) {
		if (annotationconfigs.containsKey(field.)) {
			try {
				System.out.println("Setting field " + field.getName() + " to " + value);
				field.set(null, value);

				if (field.isAnnotationPresent(Listener.class)) {
					field.getDeclaringClass().getDeclaredMethod(field.getAnnotation(Listener.class).value()).invoke(null);
				}
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				throw new RuntimeException(e);
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
