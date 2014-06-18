package ngon.game.action;

import java.util.UUID;

public class ClientConnected implements Action
{
	public final String name;
	public final UUID uuid;
	
	public ClientConnected(String name, UUID uuid)
	{
		this.name = name;
		this.uuid = uuid;
	}
	
	public String toString()
	{
		return "Client \"" + this.name + "\" connected.";
	}
	
	public String debugDetails()
	{
		return "Client name: " + this.name + "\nUUID: " + this.uuid;
	}
}
