package ngon.game.action;

import java.util.UUID;

public class ClientDisconnected implements Action
{
	private final UUID uuid;
	
	public ClientDisconnected(UUID uuid)
	{
		this.uuid = uuid;
	}
	
	public String toString()
	{
		return "Client disconnected.";
	}
	
	public String debugDetails()
	{
		return "Client UUID: " + uuid;
	}
}
