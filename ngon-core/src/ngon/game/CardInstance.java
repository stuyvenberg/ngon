package ngon.game;

import ngon.game.def.Card;

public class CardInstance
{
	public Card type;
	
	public ZoneInstance zone;
	public Player owner; // The player that loaded the card into the game.
	public Player controller; // The player currently allowed to move/reorient the card.
	public double x, y, angle;
	public boolean flipped;
	
	public CardInstance(Card type, ZoneInstance zone, double x, double y, double angle, boolean flipped)
	{
		this.type = type;
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.flipped = flipped;
	}
}
