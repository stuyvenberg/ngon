package ngon.data.def;

import java.util.UUID;

public interface Definition {
	public UUID getID();

	public Iterable<String> getPropertyNames();

	public String getProperty(String key);
	public <T> T getProperty(String key, T defaultValue);
	public <T> T getProperty(String key, Class<T> clazz) throws DefinitionException;
	public <T> void putProperty(String key, T value);
}
