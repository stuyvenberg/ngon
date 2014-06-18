package ngon.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

import ngon.game.Server;
import ngon.game.action.ActionProducer;

public class ServerListener
{
	protected final ServerConnectionThread scThread;
	protected final ServerSocket socket;
	protected final Server server;
	
	public ServerListener(Server server, int port) throws IOException
	{
		super();
		
		socket = new ServerSocket(port);
		socket.setSoTimeout(500);
		
		this.server = server;
		
		scThread = new ServerConnectionThread("ngon Server Connection Listener");
		scThread.setDaemon(true);
		scThread.start();
	}

	public String toString()
	{
		return socket.getInetAddress().toString() + ":" + socket.getLocalPort();
	}
	
	public void stop()
	{
		try {
			scThread.interrupt();
			scThread.join(5000);
		} catch(InterruptedException ie) {
			// TODO
			ie.printStackTrace();
		}
	}
	
	private class ServerConnectionThread extends Thread
	{
		public ServerConnectionThread(String name)
		{
			super(name);
		}
		
		public void run()
		{
			while(!this.isInterrupted() && !socket.isClosed()) try
			{
				new ActionTransceiver(server, socket.accept());
			}
			catch(SocketTimeoutException ste)
			{
				// Not my favorite trick, and might induce periodic activity that would otherwise be unnecessary...
				continue;
			}
			catch(IOException ioe)
			{
				// TODO
				ioe.printStackTrace();
				System.err.println("(on thread " + this.toString() + ")");
			}
			
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.err.println("(on thread " + this.toString() + ")");
				e.printStackTrace();
			}
		}
	}
}
