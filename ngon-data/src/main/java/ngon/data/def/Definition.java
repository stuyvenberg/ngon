package ngon.data.def;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Definition implements Serializable
{
	public final Map<String, Object> properties;
	public final UUID id;
	public final String name;

	public Definition(UUID uuid, String name, Map<String, Object> properties)
	{
		this.properties = properties;
		this.id = uuid;
		this.name = name;
	}

	public void putProp(String key, Object value)
	{
		properties.put(key, value);
	}
	
	public Object getProp(String key)
	{
		return properties.get(key);
	}
	
	public String toString()
	{
		return name;
	}
}
