package ngon.game.obj;

import java.util.UUID;

import ngon.data.def.AbstractCardDef;

public class Card extends GameObject
{
	public final AbstractCardDef type;
	public final Player owner; // The player that created the card.
	
	public Player controller; // The player currently allowed to move/reorient the card.
	public double x, y, angle;
	public boolean flipped;
	
	public Card(AbstractCardDef type, Player owner, double x, double y, double angle, boolean flipped)
	{
		this(UUID.randomUUID(), type, owner, owner, x, y, angle, flipped);
	}
	
	public Card(UUID instanceId, AbstractCardDef type, Player owner, Player controller, double x, double y, double angle, boolean flipped)
	{
		super(instanceId);
		
		this.type = type;
		this.owner = owner;

		this.controller = controller;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.flipped = flipped;
	}
}
