package ngon.game.action;

import java.io.Serializable;

import ngon.game.Server;

public interface Action extends Serializable
{
	public String debugDetails();
}
