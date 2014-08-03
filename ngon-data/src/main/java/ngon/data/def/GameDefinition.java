package ngon.data.def;

import java.util.UUID;

public interface GameDefinition extends Definition {
	public Iterable<ZoneDefinition> sharedZones();
	public Iterable<ZoneDefinition> instancedZones();
	
	public CardDefinition getCardPrototype();
	public SetDefinition genericSetDefinition();
	public ZoneDefinition getZoneDefinition(UUID id);
}
