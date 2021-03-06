package ngon.game.obj;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ngon.data.def.AbstractGameDef;
import ngon.data.def.ZoneDefinition;

public class Game extends GameObject
{
	private String gameName;

	public final AbstractGameDef definition;
	public final List<Zone> sharedZones;
	
	public Game(AbstractGameDef def, String name)
	{
		super(UUID.randomUUID()); // TODO
		
		this.definition = def;
		this.gameName = name;

		sharedZones = new LinkedList<Zone>();
		for(ZoneDefinition zdef : this.definition.sharedZones())
			sharedZones.add(new Zone(UUID.randomUUID(), zdef, null));
	}
}
