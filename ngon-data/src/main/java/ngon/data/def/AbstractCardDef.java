package ngon.data.def;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

public class AbstractCardDef extends AbstractDefinition implements CardDefinition
{
	public final AbstractSetDef parentSet;
	public final URI localImageURI;

	public AbstractCardDef(AbstractSetDef parentSet, UUID uuid, String name, URI imageURI, Map<String, Object> props)
	{
		super(uuid, name, props);

		this.parentSet = parentSet;
		this.localImageURI = imageURI;
	}

	@Override
	public InputStream getImageStream() throws MalformedURLException, IOException {
		return localImageURI.toURL().openStream();
	}
}
