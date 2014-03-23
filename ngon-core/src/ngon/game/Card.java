package ngon.game;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.w3c.dom.Element;

import ngon.util.xml.ElementListIterable;

public class Card
{
	public final Map<String, Object> properties;
	public final UUID uuid;
	public final Image imageFront, imageBack;

	public Card(UUID uuid, Image imageFront, Image imageBack, Object... props) throws IllegalArgumentException
	{
		if(props.length % 2 != 0)
			throw new IllegalArgumentException("Properties not of key/value form.");
		
		this.uuid = uuid;
		this.imageFront = imageFront;
		this.imageBack = imageBack;
		
		Map<String, Object> tmpProps = new HashMap<String, Object>();
		
		for(int i=0; i < props.length; i += 2)
		{
			if(!(props[i] instanceof String))
				throw new IllegalArgumentException("Key " + (i/2) + " not a string.");
			
			tmpProps.put((String) props[i], props[i+1]);
		}
		
		this.properties = Collections.unmodifiableMap(tmpProps);
	}

	public Card(Element fromXML) throws IOException, IllegalArgumentException
	{
		this.uuid = UUID.fromString(fromXML.getAttribute("uuid"));
		this.imageFront = ImageIO.read(new URL(fromXML.getAttribute("front")));
		this.imageBack = ImageIO.read(new URL(fromXML.getAttribute("back"))); // TODO: Give this a default.
		
		Map<String, Object> tmpProps = new HashMap<String, Object>();
		
		for(Element child : new ElementListIterable(fromXML.getElementsByTagName("property")))
			tmpProps.put(child.getAttribute("name"), parseValue(child));
		
		this.properties = Collections.unmodifiableMap(tmpProps);
	}

	private static Object parseValue(Element prop)
	{
		switch(prop.getAttribute("type"))
		{
			case "integer":
				return Integer.parseInt(prop.getAttribute("value"));
			case "number":
				return Double.parseDouble(prop.getAttribute("value"));
			case "string":
			default:
				return prop.getAttribute("value");
		}
	}
}
