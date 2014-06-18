package ngon.data.def;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ngon.util.xml.ElementIterable;

import org.w3c.dom.Element;

public class SetDef extends Definition
{
	public final GameDef parentGame;
	public final List<CardDef> cardDefinitions;

	public SetDef(GameDef parentGame, UUID id, String name, Map<String, Object> properties)
	{
		super(id, name, properties);
		
		this.parentGame = parentGame;
		this.cardDefinitions = new LinkedList<CardDef>();
	}
}
