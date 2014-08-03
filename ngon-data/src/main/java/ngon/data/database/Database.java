package ngon.data.database;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import ngon.data.def.CardDefinition;
import ngon.data.def.GameDefinition;
import ngon.data.def.SetDefinition;
import ngon.data.def.ZoneDefinition;

public interface Database
{
	public static class DatabaseException extends Exception
	{
		public DatabaseException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}

	public static class DatabaseError extends RuntimeException
	{
		public DatabaseError(String message, Throwable cause)
		{
			super(message, cause);
		}

		public DatabaseError(String string)
		{
			super(string);
		}

		public DatabaseError(Throwable t)
		{
			super(t);
		}
	}

	public GameDefinition game(UUID id) throws DatabaseException;
	public SetDefinition set(GameDefinition gameDef, UUID id) throws DatabaseException;

	public Iterable<GameDefinition> games() throws DatabaseException;

	public Iterable<SetDefinition> sets(GameDefinition game) throws DatabaseException;
	public Iterable<CardDefinition> cards(GameDefinition game) throws DatabaseException;

	public void load(URI source) throws DatabaseException, IOException;
	public void save(URI destination) throws DatabaseException, IOException;
}
