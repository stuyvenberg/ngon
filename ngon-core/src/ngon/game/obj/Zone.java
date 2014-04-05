package ngon.game.obj;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ngon.game.Player;
import ngon.game.def.ZoneDef;

public class Zone
{
	public final ZoneDef type;
	public final Player owner; // May be null if this is a shared zone (i.e. playing field)
	
	public List<Card> cards;
	
	public Zone(ZoneDef type, Player owner, Card... cards)
	{
		this.type = type;
		this.owner = owner;
		
		this.cards = new LinkedList<Card>();
		Collections.addAll(this.cards, cards);
	}
}
