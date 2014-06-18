package ngon.game;

import java.io.IOException;
import java.util.HashMap;

import ngon.game.action.Action;
import ngon.game.action.ActionListener;
import ngon.game.action.ActionProducer;
import ngon.game.action.ClientConnected;
import ngon.game.action.ClientDisconnected;
import ngon.game.action.ServerInfo;
import ngon.game.obj.Game;
import ngon.net.ActionTransceiver;
import ngon.net.NetListener;
import ngon.net.ServerListener;

public class Server extends ActionProducer.Simple implements ActionProducer, NetListener
{
	protected final ServerListener listener;
	protected final HashMap<String, ActionTransceiver> clients;

	public final String name;
	public Game game;

	public Server(String name, int port) throws IOException
	{
		this.name = name;

		this.listener = new ServerListener(this, port);
		this.clients = new HashMap<String, ActionTransceiver>();
	}
	
	public void stop()
	{
		listener.stop();
		
		for(ActionTransceiver client : clients.values()) try
		{
			client.disconnect();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleAction(ActionProducer source, Action act)
	{
		dispatchAction(source, act);
	}

	public void connected(ActionTransceiver tr)
	{
		clients.put(tr.name, tr);
		try
		{
			tr.send(new ServerInfo(this));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Action msg = new ClientConnected(tr.name, tr.id);
		
		for(ActionTransceiver tr2 : clients.values()) try
		{
			tr2.send(msg);
		}
		catch(IOException ioe)
		{
			// TODO
			ioe.printStackTrace();
		}
		
		handleAction(this, msg);
	}
	
	public void actionReceived(ActionTransceiver tr, Action act)
	{
		handleAction(this, act);
	}

	public void disconnected(ActionTransceiver tr)
	{
		for(String key : clients.keySet())
			if(clients.get(key).equals(tr))
				clients.remove(key);
		
		Action msg = new ClientDisconnected(tr.id);

		for(ActionTransceiver tr2 : clients.values()) try
		{
			tr2.send(msg);
		}
		catch(IOException ioe)
		{
			// TODO
			ioe.printStackTrace();
		}

		handleAction(this, msg);
	}
}
