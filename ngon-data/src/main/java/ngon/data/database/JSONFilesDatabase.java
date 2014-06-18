package ngon.data.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ngon.data.Database;
import ngon.data.def.CardDef;
import ngon.data.def.Definition;
import ngon.data.def.GameDef;
import ngon.data.def.SetDef;
import ngon.data.def.ZoneDef;
import ngon.util.array.ConcatenatingIterable;
import ngon.util.array.FilteringIterable;
import ngon.util.array.IterableUtils;
import ngon.util.array.MappingIterable;
import ngon.util.functions.Maps;
import ngon.util.functions.Predicates;
import ngon.util.functions.Predicates.Predicate;

public class JSONFilesDatabase implements Database
{
	public static class JSONArrayIterable extends JSONArray implements Iterable<Object>
	{
		public static class JSONArrayIterator implements Iterator<Object>
		{
			private final JSONArray array;
			private int index;

			public JSONArrayIterator(JSONArray array)
			{
				this.array = array;
				this.index = 0;
			}

			public boolean hasNext()
			{
				return index < array.length();
			}

			public Object next()
			{
				return array.get(index++);
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		}
		
		private static Object[] array(JSONArray jsonArray)
		{
			Object[] obj = new Object[jsonArray.length()];
			
			for(int i=0; i < jsonArray.length(); ++i)
				obj[i] = jsonArray.get(i);
			
			return obj;
		}

		public JSONArrayIterable(JSONArray jsonArray)
		{
			super(array(jsonArray));
		}

		@Override
		public Iterator<Object> iterator()
		{
			return new JSONArrayIterator(this);
		}

	}

	protected static final Comparator<GameDef> gameDefComparator = new Comparator<GameDef>()
	{
		@Override
		public int compare(GameDef a, GameDef b)
		{
			return a.name.compareTo(b.name);
		}
	};

	protected JSONObject dbObj;
	protected DefDB idDB;
	
	public JSONFilesDatabase()
	{
		idDB = new DefDB();
	}

	protected static JSONObject loadJSON(URI from) throws JSONException, FileNotFoundException
	{
		if(!from.isAbsolute())
			from = new File(".").getAbsoluteFile().toURI().resolve(from);

		return new JSONObject(new JSONTokener(new FileReader(new File(from))));
	}
	
	protected static Map<String, Object> makeMap(JSONObject obj)
	{
		if(obj == null || obj.equals(JSONObject.NULL))
			return new TreeMap<String, Object>();
		
		Map<String, Object> map = new TreeMap<String, Object>();
		
		Iterator<String> keys = obj.keys();
		while(keys.hasNext())
		{
			String key = keys.next();
			Object val = obj.get(key);

			if(val instanceof JSONObject)
				map.put(key, makeMap((JSONObject) val));
			else if(val instanceof JSONArray)
				map.put(key, makeList((JSONArray) val));
			else
				map.put(key, val);
		}
		
		return map;
	}
	
	protected static List<Object> makeList(JSONArray arr)
	{
		if(arr == null || arr.equals(JSONObject.NULL))
			return new ArrayList<Object>(0);
		
		List<Object> list = new ArrayList<Object>(arr.length());
		
		for(int i = 0; i < arr.length(); ++i)
		{
			Object val = arr.get(i);

			if(val instanceof JSONObject)
				list.add(makeMap((JSONObject) val));
			else if(val instanceof JSONArray)
				list.add(makeList((JSONArray) val));
			else
				list.add(val);
		}
		
		return list;
	}

	private GameDef parseGame(JSONObject object) throws URISyntaxException
	{
		GameDef game = new GameDef(UUID.fromString(object.getString("id")), object.getString("name"), parseCard(null, object.getJSONObject("prototype-card")), makeMap(object.getJSONObject("properties")));

		JSONArrayIterable zones = new JSONArrayIterable(object.getJSONArray("zones"));
		for (Object zonemap : zones)
		{
			if(!(zonemap instanceof JSONObject))
				throw new JSONException("Zone object not JSONObject.");
			
			game.sharedZones.add(parseZone((JSONObject) zonemap));
		}

		zones = new JSONArrayIterable(object.getJSONObject("player").getJSONArray("zones"));
		for (Object zonemap : zones)
		{
			if(!(zonemap instanceof JSONObject))
				throw new JSONException("Zone object not JSONObject.");
			
			game.playerZones.add(parseZone((JSONObject) zonemap));
		}

		return game;
	}
	
	private CardDef parseCard(SetDef parentSet, JSONObject object) throws URISyntaxException
	{
		return new CardDef(
			parentSet,
			UUID.fromString(object.getString("id")),
			object.getString("name"),
			new URI(object.getString("front")),
			new URI(object.getString("back")),
			makeMap(object.getJSONObject("properties"))
		);
	}

	private SetDef parseSet(GameDef parentGame, JSONObject object) throws URISyntaxException
	{
		SetDef set = new SetDef(parentGame, UUID.fromString(object.getString("id")), object.getString("name"), makeMap(object.optJSONObject("properties")));

		JSONArrayIterable cards = new JSONArrayIterable(object.getJSONArray("cards"));
		for (Object card : cards)
		{
			if(!(card instanceof JSONObject))
				throw new JSONException("Zone object not JSONObject.");
			
			set.cardDefinitions.add(parseCard(set, (JSONObject) card));
		}

		return set;
	}

	private ZoneDef parseZone(JSONObject object)
	{
		return new ZoneDef(UUID.fromString(object.getString("id")), object.getString("name"), makeMap(object.getJSONObject("properties")));
	}

	@Override
	public Iterable<GameDef> games()
	{
		return IterableUtils.unsafeCast(idDB.get(GameDef.class).values());
	}

	@Override
	public Iterable<SetDef> cardSets(GameDef forGame)
	{
		//@formatter:off
		return new MappingIterable<String, SetDef>(
			(Set<String>) dbObj.getJSONObject("games").getJSONObject(forGame.id.toString()).getJSONObject("sets").keySet(),
			new Maps.Map<String, SetDef>() {
				public SetDef call(String on)
				{
					return idDB.get(SetDef.class, UUID.fromString(on));
				}
			}
		);
		//@formatter:on
	}

	@Override
	public Iterable<CardDef> cards(GameDef forGame)
	{
		//@formatter:off
		return new ConcatenatingIterable<CardDef>(
			new MappingIterable<SetDef, Iterable<CardDef>>(
				cardSets(forGame),
				new Maps.Map<SetDef, Iterable<CardDef>>() {
					public Iterable<CardDef> call(SetDef on) {
						return on.cardDefinitions;
					}
				}
			)
		);
		//@formatter:on
	}

	@Override
	public Iterable<CardDef> cards(GameDef forGame, Predicate<CardDef> search)
	{
		return new FilteringIterable<CardDef>(cards(forGame), search);
	}

	@Override
	public GameDef getGameDefByID(final UUID id)
	{
		try
		{
			return parseGame(loadJSON(new URI(dbObj.getJSONObject("games").getJSONObject(id.toString()).getString("definition"))));
		}
		catch (JSONException e)
		{
			// TODO: All errors should first cause exceptions during loading.
			// Unfortunately this means checking all structure and following all links.
			// And the errors should still be thrown in case something corrupts
			// the in-memory database...
			throw new DatabaseError("Incorrect database JSON structure.", e);
		}
		catch (FileNotFoundException e)
		{
			throw new DatabaseError("Game definition file for " + id.toString() + " not found.", e);
		}
		catch (URISyntaxException e)
		{
			throw new DatabaseError("Game definition URI for " + id.toString() + " invalid.", e);
		}
	}

	@Override
	public SetDef getSetDefByID(final GameDef game, final UUID id)
	{
		if(idDB.get(SetDef.class).containsKey(id))
		{
			return idDB.get(SetDef.class, id);
		}
		else
		{
			// Check if the JSON object has it backed up.
			try
			{
				URI seturi = new URI(dbObj.getJSONObject("games").getJSONObject(game.id.toString()).getJSONObject("sets").getString(id.toString()));
				
				SetDef def = parseSet(game, loadJSON(seturi));
				idDB.put(SetDef.class, id, def);
				return def;
			}
			catch(JSONException jse)
			{
				throw new DatabaseError("Set definition entry for " + game.name + "/" + id.toString() + " is missing from database.", jse);
			}
			catch(URISyntaxException urise)
			{
				throw new DatabaseError("Set definition URI for " + game.name + "/" + id.toString() + " is invalid.", urise);
			}
			catch(FileNotFoundException jse)
			{
				throw new DatabaseError("Set definition file for " + game.name + "/" + id.toString() + " is missing.", jse);
			}
		}
	}

	@Override
	public CardDef getCardDefByID(final UUID id)
	{
		if(idDB.get(CardDef.class).containsKey(id))
		{
			return idDB.get(CardDef.class, id);
		}
		else
		{
			
		}
	}

	@Override
	public void installGame(GameDef game)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void installSet(GameDef game, SetDef set)
	{
		try
		{
			JSONObject gamesets = dbObj.getJSONObject("games").getJSONObject(game.id.toString()).getJSONObject("sets");
			
			JSONObject setobj = new JSONObject(); // This should be refactored, maybe?
			setobj.put("id", set.id.toString());
			setobj.put("name", set.name);
			
			JSONObject cardsobj = new JSONObject();
			
			for(CardDef card : set.cardDefinitions)
			{
				JSONObject cardobj = new JSONObject();
				cardobj.put("id", card.id.toString());
				cardobj.put("name", card.name);
				
				cardobj.put("front", card.imageFront.toString());
				if(card.imageBack != null)
					cardobj.put("back", card.imageBack.toString());
				
				cardobj.put("properties", new JSONObject(card.properties));
				
				cardsobj.put(card.id.toString(), cardobj);
			}
			
			gamesets.put(set.id.toString(), setobj);
		}
		catch(JSONException jse)
		{
			throw new DatabaseError("Incorrect database JSON structure.", jse);
		}
	}

	@Override
	public void load(URI source) throws IOException, DatabaseException
	{
		dbObj = loadJSON(source);
	}

	@Override
	public void save(URI destination)
	{
		// TODO Auto-generated method stub
		
	}
}
