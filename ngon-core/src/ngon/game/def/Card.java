package ngon.game.def;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.w3c.dom.Element;

public class Card extends Definition
{
	public final Image imageFront, imageBack;

	public Card(UUID uuid, String name, Image imageFront, Image imageBack, Object... props) throws IllegalArgumentException
	{
		super(uuid, name);
		
		if(props.length % 2 != 0)
			throw new IllegalArgumentException("Properties not of key/value form.");
		
		this.imageFront = imageFront;
		this.imageBack = imageBack;
	}

	public Card(Element fromXML) throws IOException, IllegalArgumentException
	{
		super(fromXML);
		
		this.imageFront = ImageIO.read(new URL(fromXML.getAttribute("front")));
		this.imageBack = ImageIO.read(new URL(fromXML.getAttribute("back"))); // TODO: Give this a default.
	}
}
