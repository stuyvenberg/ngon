package ngon.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.UUID;

import ngon.game.Client;
import ngon.game.Server;
import ngon.game.action.Action;
import ngon.game.action.ActionProducer;
import ngon.game.action.ClientConnected;
import ngon.game.action.ServerInfo;

public class ActionTransceiver
{
	private final Socket socket;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private final ClientConnectionThread ccThread;

	private final NetListener listener;
	
	public final String name;
	public final UUID id;

	public ActionTransceiver(final Client cl, String host, int port) throws UnknownHostException, IOException
	{
		super();

		socket = new Socket(host, port);
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		socket.setSoTimeout(500);
		
		this.name = cl.name;
		this.id = cl.uuid;
		this.listener = cl;
		
		out.writeObject(this.name);
		out.writeObject(this.id);
		
		listener.connected(this);

		ccThread = new ClientConnectionThread("ngon Client Thread (Explicit)");
		ccThread.setDaemon(true);
		ccThread.start();
	}

	public ActionTransceiver(final Server srv, Socket s) throws IOException
	{
		if (s == null)
			throw new NullPointerException();

		socket = s;
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		socket.setSoTimeout(500);
		
		this.listener = srv;
		String name = null; UUID id = null;
		try
		{
			name = (String) in.readObject();
			id = (UUID) in.readObject();
		}
		catch(ClassNotFoundException ioe)
		{
			ioe.printStackTrace(); // TODO
		}
		finally
		{
			if(name == null) name = "(Name Unknown)";
			if(id == null) id = UUID.randomUUID();
			
			this.name = name;
			this.id = id;
		}
		listener.connected(this);

		ccThread = new ClientConnectionThread("ngon Client Thread (Implicit)");
		ccThread.setDaemon(true);
		ccThread.start();
	}

	public String toString()
	{
		return socket.getInetAddress().toString() + ":" + socket.getPort();
	}

	protected void receive(Action act) throws IOException
	{
		listener.actionReceived(this, act);
	}

	public void send(Action act) throws IOException
	{
		out.writeObject(act);
	}

	public void disconnect() throws IOException
	{
		ccThread.disconnect();
	}

	public boolean isConnected()
	{
		return ccThread.isAlive() && !socket.isOutputShutdown();
	}

	private class ClientConnectionThread extends Thread
	{
		public ClientConnectionThread(String name)
		{
			super(name);
		}

		private void gracefulShutdown() throws IOException
		{
			socket.shutdownOutput();
			while (in.read() >= 0) ;
			socket.close();
		}

		public void disconnect() throws IOException
		{
			try
			{
				this.interrupt();
				this.join(5000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				System.err.println("(on thread " + this.toString() + ")");
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			Action act = null;

			while (!this.isInterrupted() && !socket.isClosed()) try
			{
				act = (Action) in.readObject();

				if (act == null)
					throw new NullPointerException("wut");
				else
					receive(act);
			}
			catch (SocketTimeoutException ste)
			{
				continue;
			}
			catch (EOFException eofe)
			{
				// Shhh.
				break;
			}
			catch (ClassNotFoundException cnfe)
			{
				// TODO
				System.err.println("(on thread " + this.toString() + ")");
				cnfe.printStackTrace();
			}
			catch (IOException ioe)
			{
				// TODO Auto-generated catch block
				System.err.println("(on thread " + this.toString() + ")");
				ioe.printStackTrace();
			}

			try
			{
				gracefulShutdown();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.err.println("(on thread " + this.toString() + ")");
				e.printStackTrace();
			}
			
			listener.disconnected(ActionTransceiver.this);
		}
	};
}
