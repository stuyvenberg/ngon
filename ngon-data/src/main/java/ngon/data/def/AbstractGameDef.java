package ngon.data.def;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbstractGameDef extends AbstractDefinition implements GameDefinition
{
	protected final List<AbstractZoneDef> sharedZones;
	protected final List<AbstractZoneDef> playerZones;
	protected final AbstractSetDef genericSet;
	protected final AbstractCardDef cardPrototype;

	public AbstractGameDef(UUID id, String name, AbstractCardDef cardproto, Map<String, Object> properties)
	{
		super(id, name, properties);

		this.sharedZones = new LinkedList<AbstractZoneDef>();
		this.playerZones = new LinkedList<AbstractZoneDef>();

		this.genericSet = new AbstractSetDef(this, UUID.nameUUIDFromBytes(name.getBytes()), name, null);

		this.cardPrototype = new AbstractCardDef(this.genericSet, cardproto.id, cardproto.name, cardproto.localImageURI, cardproto.properties);
	}

	@Override
	public Iterable<ZoneDefinition> sharedZones() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<ZoneDefinition> instancedZones() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CardDefinition getCardPrototype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetDefinition genericSetDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ZoneDefinition getZoneDefinition(UUID id)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
