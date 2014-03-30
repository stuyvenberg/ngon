package ngon.game.def;

import java.util.UUID;

import org.w3c.dom.Element;

public abstract class Definition
{
	public final DefinitionProperties properties;
	public final UUID uuid;
	public final String name;
	
	public Definition(UUID uuid, String name)
	{
		properties = new DefinitionProperties();
		this.uuid = uuid;
		this.name = name;
	}
	
	public Definition(Element xml)
	{
		properties = new DefinitionProperties(xml);
		uuid = UUID.fromString(xml.getAttribute("uuid"));
		this.name = xml.getAttribute("name");
	}
}
