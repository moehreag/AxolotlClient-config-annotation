package io.github.axolotlclient.AxolotlClientConfig.annotation;

/**
 * A wrapper for a config id + config class instance
 *
 * @param <C> the class of the config instance
 * @param id     the id
 * @param config the config object
 */
public record ConfigInstance<C>(String id, C config) {

    /**
     * Get this instance's attached config id.
     * This String can be used with the AxolotlClientConfigManager or the io.github.axolotlclient.AxolotlClientAnnotationConfigManager
     * and the static methods in both (they will do the exact same things).
     *
     * @return the id of this config
     */
    @Override
    public String id() {
        return id;
    }

    /**
     * Get this instance's config class instance
     *
     * @return the instance of the config class, to access your config values.
     */
    @Override
    public C config() {
        return config;
    }
}
