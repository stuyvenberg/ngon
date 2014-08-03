package ngon.data.def;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

public class AbstractDefinition implements Definition, Serializable
{
	public final Map<String, Object> properties;
	public final UUID id;
	public final String name;

	public AbstractDefinition(UUID uuid, String name, Map<String, Object> properties)
	{
		this.properties = properties;
		this.id = uuid;
		this.name = name;
	}

	@Override
	public UUID getID()
	{
		return id;
	}

	@Override
	public Iterable<String> getPropertyNames()
	{
		return properties.keySet();
	}

	public String toString()
	{
		return name;
	}

	@Override
	public String getProperty(String key)
	{
		return properties.get(key).toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key, T defaultValue)
	{
		try {
			return getProperty(key, (Class<T>) defaultValue.getClass());
		} catch(DefinitionException de) {
			return defaultValue;
		}
	}

	@Override
	public <T> T getProperty(String key, Class<T> clazz) throws DefinitionException
	{
		try {
			return clazz.cast(properties.get(key));
		} catch(ClassCastException cce) {
			throw new DefinitionException("property " + key + " of incorrect type.", cce);
		}
	}

	@Override
	public <T> void putProperty(String key, T value)
	{
		properties.put(key, value);
	}

}
