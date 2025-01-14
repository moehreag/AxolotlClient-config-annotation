package io.github.axolotlclient.AxolotlClientConfig.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.axolotlclient.AxolotlClientConfig.annotation.NamingScheme;

/**
 * Specifies an alternative name for a field/subclass to be saved as
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface SerializedName {
    /**
     * Specify the name to be used for this entry in the config file
     * @return the name of this entry
     */
    String value();

    /**
     * Specify a NamingScheme to rename fields to
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface RenameAll {
        /**
         * The scheme to rename fields to.
         * @return a NamingScheme
         * @see NamingScheme
         */
        NamingScheme value() default NamingScheme.NONE;
    }
}
