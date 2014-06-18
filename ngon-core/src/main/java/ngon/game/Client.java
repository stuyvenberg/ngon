package ngon.game;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import ngon.game.action.Action;
import ngon.game.action.ActionListener;
import ngon.game.action.ActionProducer;
import ngon.game.action.ServerDisconnected;
import ngon.game.obj.Game;
import ngon.net.ActionTransceiver;
import ngon.net.NetListener;

public class Client extends ActionProducer.Simple implements ActionProducer, NetListener
{
	protected final ActionTransceiver connection;
	protected String serverName;

	public final String name;
	public final UUID uuid;
	public Game game;
	
	public Client(String host, int port, String name) throws UnknownHostException, IOException
	{
		this.name = name;
		this.uuid = UUID.randomUUID();
		this.game = null;
		
		this.connection = new ActionTransceiver(this, host, port);
	}
	
	public String toString()
	{
		return name;
	}
	
	public String getServerName()
	{
		return serverName;
	}
	
	public void transmit(Action act) throws IOException
	{
		connection.send(act);
	}
	
	public void disconnect() 
	{
		try
		{
			connection.disconnect();
		}
		catch(IOException ioe)
		{
			// TODO
			ioe.printStackTrace();
		}
	}
	
	public void handleAction(ActionProducer source, Action act)
	{
		dispatchAction(source, act);
	}

	public void notifyServerName(String name)
	{
		serverName = name;
	}
	
	public void connected(ActionTransceiver tr)
	{
		// TODO
	}
	
	public void actionReceived(ActionTransceiver tr, Action act)
	{
		handleAction(this, act);
	}
	
	public void disconnected(ActionTransceiver tr)
	{
		dispatchAction(this, new ServerDisconnected());
	}
}
