package ngon.ui.test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;

import ngon.data.Database;
import ngon.data.def.CardDef;
import ngon.data.def.GameDef;
import ngon.data.def.SetDef;
import ngon.util.functions.Predicates;
import ngon.util.functions.Predicates.Predicate;
import ngon.util.xml.XmlTools;

public class TestDatabase implements Database
{
	public static final String TEST_CARD_NAME = "Test Card";
	public static final String TEST_GAME_NAME = "Test Game";

	private static final GameDef testGame;
	private static final CardDef testCard;

	static {
		GameDef bugger = null;
		CardDef buggerTo = null;
		try
		{
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("Type", "Lawl");
			props.put("Cost", "WUBRG");
			props.put("Rules Text", "You r teh winnar!");

			buggerTo = new CardDef(
				null,
				UUID.nameUUIDFromBytes(TEST_CARD_NAME.getBytes()),
				TEST_CARD_NAME,
				new URI("img/mtg-front.jpg"),
				new URI("img/mtg-back.jpg"),
				props
			);

			Map<String, Object> gameprops = new HashMap<String, Object>();
			gameprops.put("default-properties", "Type,Cost,Rules Text");
			bugger = new GameDef(
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
	public Iterable<GameDef> games()
	{
		return Arrays.asList(testGame);
	}

	@Override
	public Iterable<SetDef> cardSets(GameDef forGame)
	{
		// TODO Auto-generated method stub
		return new LinkedList<SetDef>();
	}

	@Override
	public Iterable<CardDef> cards(GameDef forGame)
	{
		// TODO Auto-generated method stub
		return new LinkedList<CardDef>(Arrays.asList(testCard));
	}

	@Override
	public Iterable<CardDef> cards(GameDef forGame, Predicate<CardDef> search)
	{
		// TODO Auto-generated method stub
		return new LinkedList<CardDef>(Arrays.asList(testCard));
	}

	@Override
	public GameDef getGameDefByID(UUID id)
	{
		// TODO Auto-generated method stub
		return testGame;
	}

	@Override
	public CardDef getCardDefByID(UUID id)
	{
		// TODO Auto-generated method stub
		return testCard;
	}

	@Override
	public SetDef getSetDefByID(GameDef game, UUID id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void installGame(GameDef game) throws DatabaseException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void installSet(GameDef game, SetDef set) throws DatabaseException
	{
		// TODO Auto-generated method stub
		
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
