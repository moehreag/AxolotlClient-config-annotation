package io.github.axolotlclient.config.annotation;

public class ConfigInstance<C> {

	private final String id;
	private final C config;


	public ConfigInstance(String id, C config) {
		this.id = id;
		this.config = config;
	}

	public String getId() {
		return id;
	}

	public C getConfig() {
		return config;
	}
}
