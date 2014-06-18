package ngon.game.action;

import java.util.UUID;

import ngon.game.Server;

public class ServerInfo implements Action
{
	public final String serverName;
	public final UUID gameDefUUID;
	
	public ServerInfo(Server of)
	{
		this.serverName = of.name;
		this.gameDefUUID = (of.game != null ? of.game.definition.id : null);
	}

	public String toString()
	{
		return "Connected to server " + serverName;
	}
	
	public String debugDetails()
	{
		if(gameDefUUID != null)
			return "Running game " + gameDefUUID.toString();
		else
			return "No active game.";
	}
}
