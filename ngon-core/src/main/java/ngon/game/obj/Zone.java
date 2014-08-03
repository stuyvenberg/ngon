package ngon.game.obj;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ngon.data.def.AbstractZoneDef;
import ngon.data.def.ZoneDefinition;
import ngon.util.NonGameObjRefUUID;

public class Zone extends GameObject
{
	public final ZoneDefinition definition;
	public final Player owner; // May be null if this is a shared zone (i.e. playing field)
	public Player controller; // May also be null for the above reason.
	
	public List<Card> cards;
	
	public Zone(UUID id, ZoneDefinition def, Player owner, Card... cards)
	{
		super(id);

		this.definition = def;
		this.owner = owner;
		this.controller = owner;
		
		this.cards = new LinkedList<Card>();
		Collections.addAll(this.cards, cards);
	}
	
	public String toString()
	{
		String ret = definition.toString();
		
		if(this.owner != null)
			ret = String.format("%s's %s", owner.name, definition.toString());
		
		return ret;
	}
}
