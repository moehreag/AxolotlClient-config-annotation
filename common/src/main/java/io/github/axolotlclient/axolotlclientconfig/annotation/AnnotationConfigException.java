package io.github.axolotlclient.axolotlclientconfig.annotation;

/**
 * Exception for when creating the config fails.
 */
public class AnnotationConfigException extends IllegalStateException {
    /**
     * @param s String explaining the reason
     */
    public AnnotationConfigException(String s) {
        super(s);
    }

    /**
     * Create an exception
     */
    public AnnotationConfigException() {
        super();
    }

    /**
     * @param message Message explaining the need
     * @param cause a throwable to throw
     */
    public AnnotationConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause a throwable that might get thrown out of the window
     */
    public AnnotationConfigException(Throwable cause) {
        super(cause);
    }
}
