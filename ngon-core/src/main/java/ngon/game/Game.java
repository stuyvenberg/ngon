package ngon.game;

import java.util.List;

public class Game
{
	public final String name;
	
	public Game(String name)
	{
		this.name = name;
	}
	
	public List<Player> playerList; // TODO: Teams?
	
	public static Game hostGame(String name, int port)
	{
		// TODO stub
		return null;
	}
}
