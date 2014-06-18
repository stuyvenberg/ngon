package ngon.game.obj;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ngon.data.def.GameDef;
import ngon.data.def.ZoneDef;

public class Game extends GameObject
{
	private String gameName;

	public final GameDef definition;
	public final List<Zone> sharedZones;
	
	public Game(GameDef def, String name)
	{
		super(UUID.randomUUID()); // TODO
		
		this.definition = def;
		this.gameName = name;

		sharedZones = new LinkedList<Zone>();
		for(ZoneDef zdef : this.definition.sharedZones)
			sharedZones.add(new Zone(UUID.randomUUID(), zdef, null));
	}
}
