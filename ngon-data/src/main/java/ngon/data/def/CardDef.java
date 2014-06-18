package ngon.data.def;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Element;

public class CardDef extends Definition
{
	public final SetDef parentSet;
	public final URI imageFront, imageBack;

	public CardDef(SetDef parentSet, UUID uuid, String name, URI imageFront, URI imageBack, Map<String, Object> props)
	{
		super(uuid, name, props);

		this.parentSet = parentSet;
		this.imageFront = imageFront;
		this.imageBack = imageBack;
	}
}
