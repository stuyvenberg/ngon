package ngon.game.def;

import java.util.UUID;

import org.w3c.dom.Element;

public abstract class Definition
{
	public final DefinitionProperties properties;
	public final UUID uuid;
	public final String name;
	
	public Definition(UUID uuid, String name, Object... properties)
	{
		this.properties = new DefinitionProperties(properties);
		this.uuid = uuid;
		this.name = name;
	}
	
	public Definition(Element xml)
	{
		this.properties = new DefinitionProperties(xml);
		this.uuid = UUID.fromString(xml.getAttribute("uuid"));
		this.name = xml.getAttribute("name");
	}
}
