package ngon.game.obj;

import java.util.UUID;

import ngon.game.Player;
import ngon.game.def.CardDef;

public class Card
{
	public final CardDef type;
	public final UUID uuid;
	public final Player owner; // The player that created the card.
	
	public Player controller; // The player currently allowed to move/reorient the card.
	public double x, y, angle;
	public boolean flipped;
	
	public Card(CardDef type, Player owner, double x, double y, double angle, boolean flipped)
	{
		this.type = type;
		this.uuid = UUID.randomUUID(); // TODO: Is this the best option?
		this.owner = owner;

		this.controller = owner;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.flipped = flipped;
	}
}
