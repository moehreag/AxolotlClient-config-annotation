package io.github.axolotlclient.AxolotlClientConfig.annotation;

/**
 * Exception for when creating the config fails.
 */
class AnnotationConfigException extends IllegalStateException {
    /**
     * @param message String explaining the reason
     * @param cause a throwable that might get thrown out of the window
     */
    public AnnotationConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause a throwable that might get thrown out of the window
     */
    AnnotationConfigException(Throwable cause) {
        super(cause);
    }
}
