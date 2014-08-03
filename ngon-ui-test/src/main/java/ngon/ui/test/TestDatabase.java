package ngon.ui.test;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ngon.data.database.Database;
import ngon.data.def.AbstractCardDef;
import ngon.data.def.AbstractGameDef;
import ngon.data.def.CardDefinition;
import ngon.data.def.GameDefinition;
import ngon.data.def.SetDefinition;

public class TestDatabase implements Database
{
	public static final String TEST_CARD_NAME = "Test Card";
	public static final String TEST_GAME_NAME = "Test Game";

	private static final AbstractGameDef testGame;
	private static final AbstractCardDef testCard;

	static {
		AbstractGameDef bugger = null;
		AbstractCardDef buggerTo = null;
		try
		{
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("Type", "Lawl");
			props.put("Cost", "WUBRG");
			props.put("Rules Text", "You r teh winnar!");

			buggerTo = new AbstractCardDef(
				null,
				UUID.nameUUIDFromBytes(TEST_CARD_NAME.getBytes()),
				TEST_CARD_NAME,
				new URI("img/mtg-front.jpg"),
				props
			);

			Map<String, Object> gameprops = new HashMap<String, Object>();
			gameprops.put("default-properties", "Type,Cost,Rules Text");
			bugger = new AbstractGameDef(
				UUID.nameUUIDFromBytes(TEST_GAME_NAME.getBytes()),
				TEST_GAME_NAME,
				buggerTo,
				props
			);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testGame = bugger;
		testCard = buggerTo;
	}

	@Override
	public GameDefinition game(UUID id) throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetDefinition set(GameDefinition gameDef, UUID id) throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<GameDefinition> games() throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SetDefinition> sets(GameDefinition game) throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<CardDefinition> cards(GameDefinition game) throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(URI source) throws DatabaseException, IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void save(URI destination) throws DatabaseException, IOException
	{
		// TODO Auto-generated method stub

	}

}
