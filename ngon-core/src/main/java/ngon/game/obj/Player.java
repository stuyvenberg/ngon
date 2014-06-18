package ngon.game.obj;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ngon.util.NonGameObjRefUUID;

public class Player extends GameObject
{
	/**
	 * Player's printing name, chosen prior to connection and unchangeable mid-game.
	 */
	public final String name;

	public Map<String, Zone> zones;
	
	public Player(UUID uuid, String name)
	{
		super(uuid);

		this.name = name;
		this.zones = new HashMap<String, Zone>();
	}
}
