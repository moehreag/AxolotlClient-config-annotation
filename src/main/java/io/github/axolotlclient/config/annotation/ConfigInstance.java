package io.github.axolotlclient.config.annotation;

/**
 * A wrapper for a config id + config class instance
 * @param <C> the class of the config instance
 */
public class ConfigInstance<C> {

	private final String id;
	private final C config;

	ConfigInstance(String id, C config) {
		this.id = id;
		this.config = config;
	}

    /**
     * Get this instance's attached config id.
     * This String can be used with the AxolotlClientConfigManager or the AxolotlClientAnnotationConfigManager
     * and the static methods in both (they will do the exact same things).
     * @return the id of this config
     */
	public String getId() {
		return id;
	}

    /**
     * Get this instance's config class instance
     * @return the instance of the config class, to access your config values.
     */
	public C getConfig() {
		return config;
	}
}
