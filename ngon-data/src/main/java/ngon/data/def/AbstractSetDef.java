package ngon.data.def;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbstractSetDef extends AbstractDefinition implements SetDefinition
{
	private final AbstractGameDef parentGame;
	private final List<CardDefinition> cardDefinitions;

	public AbstractSetDef(AbstractGameDef parentGame, UUID id, String name, Map<String, Object> properties)
	{
		super(id, name, properties);

		this.parentGame = parentGame;
		this.cardDefinitions = new LinkedList<CardDefinition>();
	}

	@Override
	public Iterable<CardDefinition> getCards() {
		return cardDefinitions;
	}
}
