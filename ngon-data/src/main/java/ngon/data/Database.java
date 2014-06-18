package ngon.data;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ngon.data.def.CardDef;
import ngon.data.def.Definition;
import ngon.data.def.GameDef;
import ngon.data.def.SetDef;
import ngon.util.functions.Predicates;

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
	}

	public static class DefDB extends HashMap<Class<? extends Definition>, Map<UUID, ? extends Definition>> {
		@SuppressWarnings("unchecked")
		public <T extends Definition> Map<UUID, T> get(Class<T> type)
		{
			return (Map<UUID, T>) get(type);
		}

		public <T extends Definition> T get(Class<T> type, UUID id)
		{
			return ((Map<UUID, T>) get(type)).get(id);
		}
		
		public <T extends Definition> void put(Class<T> type, UUID id, T object)
		{
			if(get(type) == null)
				put(type, new HashMap<UUID, T>());

			((Map<UUID, T>) get(type)).put(id, object);
		}

		public Map<UUID, ? extends Definition> put(Class<? extends Definition> type, Map<UUID, ? extends Definition> map)
		{
			throw new UnsupportedOperationException("Sub-maps are managed internally.");
		}
	}

	public Iterable<GameDef> games();

	public Iterable<SetDef> cardSets(GameDef forGame);

	public Iterable<CardDef> cards(GameDef forGame);
	public Iterable<CardDef> cards(GameDef forGame, Predicates.Predicate<CardDef> search);

	public GameDef getGameDefByID(UUID id);
	public SetDef getSetDefByID(GameDef game, UUID id);
	public CardDef getCardDefByID(UUID id);

	public void installGame(GameDef game) throws DatabaseException;
	public void installSet(GameDef game, SetDef set) throws DatabaseException;

	public void load(URI source) throws DatabaseException, IOException;
	public void save(URI destination) throws DatabaseException, IOException;
}
