package io.github.axolotlclient.config.annotation;

import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.ConfigHolder;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.config.annotation.annotations.Config;
import io.github.axolotlclient.config.annotation.annotations.DoubleRange;
import io.github.axolotlclient.config.annotation.annotations.FloatRange;
import io.github.axolotlclient.config.annotation.annotations.IntRange;

import java.lang.reflect.Field;
import java.util.*;

public class AxolotlClientAnnotationConfigManager {

	private static final Map<Class<?>, OptionCategory> annotationconfigs = new HashMap<>();

	public static void registerConfig(Class<?> config){
		if(!config.isAnnotationPresent(Config.class)){
			throw new IllegalArgumentException("Not a config Class!");
		}

		String name = config.getAnnotation(Config.class).name().isEmpty() ?
				config.getSimpleName() : config.getAnnotation(Config.class).name();
		annotationconfigs.put(config, generateCategory(name, config));
		AxolotlClientConfigManager.registerConfig(name, new ConfigHolder() {
			@Override
			public List<OptionCategory> getCategories() {
				return Collections.singletonList(annotationconfigs.get(config));
			}
		});
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
				return new BooleanOption(field.getName(), field.getBoolean(null));
			} else if (field.getType().getSimpleName().equalsIgnoreCase("string")){
				return new StringOption(field.getName(), (String) field.get(null));
			} else if (field.getType()
					.getSimpleName().toLowerCase(Locale.ROOT).contains("int")){
				if(field.isAnnotationPresent(IntRange.class)){
					return new IntegerOption(field.getName(), (Integer) field.get(null), field.getAnnotation(IntRange.class).min(), field.getAnnotation(IntRange.class).max());
				}
				return new IntegerOption(field.getName(), (Integer) field.get(null), 0, 10);
			} else if (field.getType().getSimpleName().equalsIgnoreCase("float")){
				if(field.isAnnotationPresent(FloatRange.class)){
					return new FloatOption(field.getName(), (Float) field.get(null), field.getAnnotation(FloatRange.class).min(), field.getAnnotation(FloatRange.class).max());
				}
				return new FloatOption(field.getName(), (Float) field.get(null), 0F, 10F);
			} else if (field.getType().getSimpleName().equalsIgnoreCase("double")){
				if(field.isAnnotationPresent(DoubleRange.class)){
					return new DoubleOption(field.getName(), (Double) field.get(null), field.getAnnotation(DoubleRange.class).min(), field.getAnnotation(DoubleRange.class).max());
				}
				return new DoubleOption(field.getName(), (Double) field.get(null), 0F, 10F);
			} else if (field.get(null) instanceof Color){
				return new ColorOption(field.getName(), (Color) field.get(null));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
