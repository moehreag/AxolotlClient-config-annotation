package io.github.axolotlclient.AxolotlClientConfig.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An optional annotation adding a listener to a field.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Listener {

	/**
	 * Define a listener method for an option. This method will be invoked reflectively.
	 * @return A method name in the same class as this field, it will be called when the option related to this field changes.
	 *  The method has to have the structure of
	 *  {@code
	 *      <any access modified> void method(<type of your field> newValue){
	 *          ...
	 *      }
	 *  }
	 */
	String value();
}
