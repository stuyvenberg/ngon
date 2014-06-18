package ngon.data.def;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ngon.util.array.IterableUtils;
import ngon.util.xml.ElementIterable;
import ngon.util.xml.XmlTools;

import org.w3c.dom.Element;

public class GameDef extends Definition
{
	public final List<ZoneDef> sharedZones;
	public final List<ZoneDef> playerZones;
	public final SetDef genericSet;
	public final CardDef cardPrototype;

	public GameDef(UUID id, String name, CardDef cardproto, Map<String, Object> properties)
	{
		super(id, name, properties);

		this.sharedZones = new LinkedList<ZoneDef>();
		this.playerZones = new LinkedList<ZoneDef>();

		this.genericSet = new SetDef(this, UUID.nameUUIDFromBytes(name.getBytes()), name, null);

		this.cardPrototype = new CardDef(this.genericSet, cardproto.id, cardproto.name, cardproto.imageFront, cardproto.imageBack, cardproto.properties);
	}
}
