package ngon.data.def;

import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Element;

public class AbstractZoneDef extends AbstractDefinition implements ZoneDefinition
{
	public AbstractZoneDef(UUID uuid, String name, Map<String, Object> properties)
	{
		super(uuid, name, properties);
		// TODO Auto-generated constructor stub
	}
}
