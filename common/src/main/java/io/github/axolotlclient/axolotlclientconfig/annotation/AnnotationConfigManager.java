package io.github.axolotlclient.axolotlclientconfig.annotation;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.options.*;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.DoubleRange;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.FloatRange;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.IntRange;
import io.github.axolotlclient.axolotlclientconfig.annotation.annotations.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface AnnotationConfigManager {

    <C> ConfigInstance<C> registerConfig(Class<C> config);

    default OptionCategory generateCategory(String name, Class<?> clazz, Object conf, Object declaringClass){
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

    default Option<?> getOption(Field field, Object clazz, Object configObject){
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
                return new DoubleOption(field.getName(), value -> setField(field, value, clazz, configObject), (Double) val, (double) getSliderDefaultMin(), (double) getSliderDefaultMax());
            } else if (val instanceof Color) {
                return new ColorOption(field.getName(), value -> setField(field, value, clazz, configObject), (Color) val);
            } else if (val.getClass().isEnum()) {
                return new EnumOption(field.getName(), value -> {
                    try {
                        setField(field,
                                (val.getClass().getMethod("valueOf", String.class).invoke(clazz, value)), clazz, configObject);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        error("This should not have happened, how did you create an enum without accessible 'valueOf' Method?");
                    }
                },
                        Arrays.stream(val.getClass().getEnumConstants()).map(Object::toString).toArray(String[]::new), val.toString());
            } else if (val instanceof int[][]){
                return new GraphicsOption(field.getName(), value -> setField(field, value, clazz, configObject), (int[][]) val);
            }
            return getOptionVersioned(field, clazz, configObject, val);
        } catch (Exception e){
            warn("Unsupported field in config class "+clazz.getClass().getName()+": "+field.getName());
        }
        return null;
    }

    Option<?> getOptionVersioned(Field field, Object clazz, Object configObject, Object value);

    default  <T> void setField(Field field, T value, Object declaringClass, Object configObject) {
        if (isRegistered(configObject)) {
            try {
                field.set(declaringClass, value);
                if (field.isAnnotationPresent(Listener.class)) {
                    try {
                        field.getDeclaringClass().getDeclaredMethod(field.getAnnotation(Listener.class).value(), field.getType()).invoke(declaringClass, value);
                    } catch (ReflectiveOperationException e){
                        error("Listener Method '"+field.getAnnotation(Listener.class).value()+"' could not be found or has the wrong parameters!");
                    }
                }
                if(!field.get(declaringClass).equals(value)){
                    error("Field "+field.getName()+" could not be set to its new value!");
                }
            } catch (IllegalAccessException e) {
                error("Field "+field.getName()+" could not be set to its new value!");
            }
        }
    }

    boolean isRegistered(Object o);

    default int getSliderDefaultMin(){
        return 0;
    }

    default int getSliderDefaultMax(){
        return 10;
    }

    class AnnotationConfigHolder extends ConfigHolder {

        private final OptionCategory config;

        public AnnotationConfigHolder(OptionCategory config){
            this.config = config;
        }

        @Override
        public List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> getCategories() {
            return Collections.singletonList(config);
        }
    }

    void error(String msg);

    void warn(String msg);

    void info(String msg);

}
