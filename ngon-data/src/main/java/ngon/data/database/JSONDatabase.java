package ngon.data.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import ngon.data.def.CardDefinition;
import ngon.data.def.Definition;
import ngon.data.def.DefinitionException;
import ngon.data.def.GameDefinition;
import ngon.data.def.SetDefinition;
import ngon.data.def.ZoneDefinition;

public class JSONDatabase implements Database
{
	protected static class JSONDef implements Definition
	{
		protected final UUID uuid;
		protected JsonObject obj;

		public JSONDef()
		{
			obj = new JsonObject();
			obj.add("properties", new JsonObject());
			uuid = UUID.randomUUID();
		}

		public JSONDef(UUID id, File source) throws IOException
		{
			this(id, new FileInputStream(source));
		}

		public JSONDef(UUID id, InputStream source) throws IOException
		{
			uuid = id;
			obj = JsonObject.readFrom(new InputStreamReader(source));

			if(obj.get("properties") == null)
				obj.add("properties", new JsonObject());

			source.close();
		}

		public JSONDef(UUID id, JsonObject wrap)
		{
			uuid = id;
			obj = wrap;

			if(obj.get("properties") == null)
				obj.add("properties", new JsonObject());
		}

		@Override
		public UUID getID()
		{
			return uuid;
		}

		@Override
		public String toString()
		{
			return obj.get("name").asString();
		}

		@Override
		public Iterable<String> getPropertyNames()
		{
			return getSubObject("properties").names();
		}

 		@Override
		@SuppressWarnings("unchecked")
		public <T> T getProperty(String key, T defaultValue)
		{
			try {
				T obj = getProperty(key, (Class<T>) defaultValue.getClass());
				return obj == null ? defaultValue : obj;
			} catch(DefinitionException de) {
				return defaultValue;
			}
		}

 		@Override
		public <T> T getProperty(String key, Class<T> clazz) throws DefinitionException
		{
			try {
				return clazz.cast(getGeneric(getSubObject("properties"), key)); // Is there any advantage to clazz.cast over (T)obj?
			} catch(ClassCastException cce) {
				throw new DefinitionException("Property " + key + " of invalid type.", cce);
			}
		}

		@Override
		public <T> void putProperty(String key, T value)
		{
			JsonObject to = getSubObject("properties").asObject();

			if(value == null)
				to.set(key, JsonValue.NULL);
			else if(value instanceof Number)
				to.set(key, ((Number) value).doubleValue());
			else if(value instanceof Boolean)
				to.set(key, (Boolean) value);
			else if(value instanceof String)
				to.set(key, value.toString());
			else if(value instanceof Iterable<?>)
				to.set(key, makeArray(Iterables.transform((Iterable<?>) value, val -> JsonValue.valueOf(val.toString()))));
			else if(value instanceof Object[])
				to.set(key, makeArray(Iterables.transform(Arrays.asList((Object[]) value), val-> JsonValue.valueOf(val.toString()))));
			else
				; // ???
		}

		protected JsonObject obj()
		{
			return obj;
		}

		protected JsonObject getSubObject(String key)
		{
			return obj.get(key).asObject();
		}

		protected JsonArray getSubArray(String key)
		{
			return obj.get(key).asArray();
		}

		protected static Object getGeneric(JsonObject from, String key)
 		{
			JsonValue val = from.get(key);

			if(val == null || val.isNull())
				return null;
			else if(val.isBoolean())
				return val.asBoolean();
			else if(val.isNumber())
				return val.asDouble(); // ???
			else if(val.isString())
				return val.asString();
			else if(val.isArray())
				return Iterables.toArray(Iterables.transform(val.asArray(), v -> v.asString()), String.class);
			else
				return null;
 		}

		protected static JsonArray makeArray(Iterable<JsonValue> values)
		{
			JsonArray arr = new JsonArray();

			for(JsonValue val : values)
				arr.add(val);

			return arr;
		}
	}

	public class JSONGameDef extends JSONDef implements GameDefinition
	{
		// How horrifying.
		public JSONGameDef() { super(); }
		public JSONGameDef(UUID id, File source) throws IOException { super(id, source); }
		public JSONGameDef(UUID id, InputStream source) throws IOException { super(id, source); }
		public JSONGameDef(UUID id, JsonObject wrap) { super(id, wrap); }

		// TODO: The getters in here always create new objects. This isn't pretty,
		// but it should be functional, since each JSONDef should be referencing the
		// same JsonObject beneath.

		@Override
		public Iterable<ZoneDefinition> sharedZones()
		{
			return Iterables.transform(getSubObject("zones"), val -> new JSONZoneDef(UUID.fromString(val.getName()), val.getValue().asObject()));
		}

		@Override
		public Iterable<ZoneDefinition> instancedZones()
		{
			return Iterables.transform(getSubObject("player").get("zones").asObject(), val -> new JSONZoneDef(UUID.fromString(val.getName()), val.getValue().asObject()));
		}

		@Override
		public CardDefinition getCardPrototype()
		{
			JsonObject proto = getSubObject("prototype-card");
			return new JSONCardDef(UUID.fromString(proto.get("id").asString()), proto);
		}

		@Override
		public SetDefinition genericSetDefinition()
		{
			JsonObject genset = getSubObject("generic-set");
			return new JSONSetDef(UUID.fromString(genset.get("id").asString()), genset);
		}

		@Override
		public ZoneDefinition getZoneDefinition(UUID id)
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class JSONSetDef extends JSONDef implements SetDefinition
	{
		public JSONSetDef() { super(); }
		public JSONSetDef(UUID id, File source) throws IOException { super(id, source); }
		public JSONSetDef(UUID id, InputStream source) throws IOException { super(id, source); }
		public JSONSetDef(UUID id, JsonObject wrap) { super(id, wrap); }

		@Override
		public Iterable<CardDefinition> getCards()
		{
			return Iterables.transform(getSubObject("cards"), card -> new JSONCardDef(UUID.fromString(card.getName()), card.getValue().asObject()));
		}
	}

	public class JSONCardDef extends JSONDef implements CardDefinition
	{
		public JSONCardDef() { super(); }
		public JSONCardDef(UUID id, File source) throws IOException { super(id, source); }
		public JSONCardDef(UUID id, InputStream source) throws IOException { super(id, source); }
		public JSONCardDef(UUID id, JsonObject wrap) { super(id, wrap); }

		@Override
		public InputStream getImageStream() throws IOException
		{
			try
			{
				// TODO: I'll need to somehow clarify "this is an image URI" vs "this is a JSON URI" vs etc.
				return JSONDatabase.this.openURI(new URI(obj().get("image").asString()));
			}
			catch (URISyntaxException e)
			{
				throw new IOException("invalid URI when loading image for " + this.toString(), e);
			}
		}
	}

	public class JSONZoneDef extends JSONDef implements ZoneDefinition
	{
		public JSONZoneDef() { super(); }
		public JSONZoneDef(UUID id, File source) throws IOException { super(id, source); }
		public JSONZoneDef(UUID id, InputStream source) throws IOException { super(id, source); }
		public JSONZoneDef(UUID id, JsonObject wrap) { super(id, wrap); }
	}

	private JsonObject obj;
	private File file;

	protected InputStream openURI(URI in) throws URISyntaxException, IOException
	{
		switch (in.getScheme() != null ? in.getScheme() : "file")
		{
		case "zip":
			{
				// TODO: I should really cache a ZipFile whenever this happens.
				String[] parts = in.getSchemeSpecificPart().split("!");
				URI zipuri = new URI(Joiner.on('!').join(Arrays.asList(parts).subList(0, parts.length - 1)));
				File entry = new File("/", parts[parts.length - 1]);
				ZipInputStream input = new ZipInputStream(openURI(zipuri));

				for (ZipEntry e = input.getNextEntry(); e != null; e = input.getNextEntry())
				{
					System.out.println("zip entry: " + new File("/", e.getName()));
					if (new File("/", e.getName()).equals(entry))
						return input;
					else
						input.closeEntry();
				}

				throw new IOException("Zip file " + zipuri.toString() + " does not contain an entry " + entry);
			}
		case "file":
			return new FileInputStream(new File(file.getParentFile().toURI().resolve(in)));
		case "http":
			return in.toURL().openStream();
		default:
			throw new IOException("Unknown URI scheme " + in.getScheme());
		}
	}

	@Override
	public GameDefinition game(UUID id) throws DatabaseException
	{
		try {
			return new JSONGameDef(id, openURI(new URI(obj.get("games").asObject().get("definition").asString())));
		} catch(UnsupportedOperationException uoe) {
			throw new DatabaseError(uoe);
		} catch(URISyntaxException urise) {
			throw new DatabaseError(urise);
		} catch(IOException ioe) {
			throw new DatabaseError(ioe);
		}
	}

	@Override
	public SetDefinition set(GameDefinition def, UUID id) throws DatabaseException
	{
		try {
			JsonObject sets = obj.get("games").asObject().get(def.getID().toString()).asObject().get("sets").asObject();
			return new JSONSetDef(id, openURI(new URI(sets.get(id.toString()).asString())));
		} catch(UnsupportedOperationException uoe) {
			throw new DatabaseError(uoe);
		} catch(URISyntaxException urise) {
			throw new DatabaseError(urise);
		} catch(IOException ioe) {
			throw new DatabaseError(ioe);
		}
	}

	// TODO: This is a case where always returning a new def instance is not okay; the underlying
	// JsonObjects will vary between instances, so there will be no consistency.
	@Override
	public Iterable<GameDefinition> games() throws DatabaseException
	{
		JsonObject games = obj.get("games").asObject();
		return Iterables.transform(games, member -> {
			try {
				return new JSONGameDef(UUID.fromString(member.getName()), openURI(new URI(member.getValue().asObject().get("definition").asString())));
			} catch(URISyntaxException urise) {
				throw new DatabaseError(urise);
			} catch(IOException ioe) {
				throw new DatabaseError(ioe);
			}
		});
	}

	@Override
	public Iterable<SetDefinition> sets(GameDefinition game) throws DatabaseException
	{
		JsonObject sets = obj.get("games").asObject().get(game.getID().toString()).asObject().get("sets").asObject();
		return Iterables.transform(sets, member -> {
			try {
				return new JSONSetDef(UUID.fromString(member.getName()), openURI(new URI(member.getValue().asString())));
			} catch (IOException | URISyntaxException e) {
				throw new DatabaseError(e);
			}
		});
	}

	@Override
	public Iterable<CardDefinition> cards(GameDefinition game) throws DatabaseException
	{
		return Iterables.concat((Iterable<Iterable<CardDefinition>>) Iterables.transform(sets(game), set -> set.getCards()));
	}

	@Override
	public void load(URI source) throws DatabaseException, IOException
	{
		obj = JsonObject.readFrom(new FileReader(file = new File(source)));
	}

	@Override
	public void save(URI destination) throws DatabaseException, IOException
	{
		obj.writeTo(new FileWriter(destination == null ? file : (file = new File(destination))));
	}
}
