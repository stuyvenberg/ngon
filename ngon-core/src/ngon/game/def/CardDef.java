package ngon.game.def;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.w3c.dom.Element;

public class CardDef extends Definition
{
	public final Image imageFront, imageBack;

	public CardDef(UUID uuid, String name, Image imageFront, Image imageBack, Object... props) throws IllegalArgumentException
	{
		super(uuid, name);
		
		this.imageFront = imageFront;
		this.imageBack = imageBack;
	}

	public CardDef(Element fromXML) throws IOException, IllegalArgumentException
	{
		super(fromXML);
		
		this.imageFront = ImageIO.read(new URL(fromXML.getAttribute("front")));
		this.imageBack = ImageIO.read(new URL(fromXML.getAttribute("back"))); // TODO: Give this a default, from the game?
	}
}
