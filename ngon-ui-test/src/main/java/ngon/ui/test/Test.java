package ngon.ui.test;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ngon.data.database.Database;
import ngon.data.database.Database.DatabaseException;
import ngon.data.database.JSONDatabase;
import ngon.game.action.Action;
import ngon.game.action.ActionProducer;

public class Test extends JFrame implements java.awt.event.ActionListener, ChangeListener, ItemListener, ListSelectionListener
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		(new Test()).setVisible(true);
	}

	private JTextField serverName;
	private JSpinner serverPort;
	private JToggleButton serverRunning;
	private JList<EventLog> serverEventList;

	private JTextField clientName;
	private JTextField clientHostName;
	private JSpinner clientHostPort;
	private JToggleButton clientConnected;
	private JList<EventLog> clientEventList;

	private JTextArea details;
	private JTextArea globalLog;

	private ngon.game.Server serverServer;

	private ngon.game.Client clientClient;
	private ngon.game.obj.Player clientPlayer;

	public Test()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("ngon - Test UI");
		
		JPanel content = new JPanel();
		content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		setContentPane(content);
		
		JButton deckBuilderTest = new JButton("Deck Builder Test");
		deckBuilderTest.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try
				{
					Database db = new JSONDatabase();
					db.load(new File("src/test/java/test-stuff/db-test.json").toURI());
					new DeckBuilder(Test.this, db).setVisible(true);
				}
				catch (DatabaseException | IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//@formatter:off
		LayoutUtils.addAll(getContentPane(),
			LayoutUtils.horiz(
				LayoutUtils.vert("Server",
					LayoutUtils.form(
						"Name:", serverName = new JTextField(32),
						"Port:", serverPort = new JSpinner(new SpinnerNumberModel(8088, 1024, 65535, 1)),
						"Running:", serverRunning = new JToggleButton("Running")
					),
					new JScrollPane(serverEventList = new JList<EventLog>(new DefaultListModel<EventLog>()))
				),
				LayoutUtils.vert("Client",
					LayoutUtils.form(
						"Player name:", clientName = new JTextField(32),
						"Host:", LayoutUtils.horiz(
							clientHostName = new JTextField("localhost"),
							clientHostPort = new JSpinner(new SpinnerNumberModel(8088, 1024, 65535, 1))
						),
						"Connected:", clientConnected = new JToggleButton("Connected")
					),
					new JScrollPane(clientEventList = new JList<EventLog>(new DefaultListModel<EventLog>()))
				)
			),
			LayoutUtils.titled("Event Details", new JScrollPane(details = new JTextArea("Select an event to view details.", 16, 64))),
			LayoutUtils.titled("Console", new JScrollPane(globalLog = new JTextArea("", 16, 64))),
			deckBuilderTest
		);
		//@formatter:on
		
		serverPort.setEditor(new JSpinner.NumberEditor(serverPort, "#"));
		clientHostPort.setEditor(new JSpinner.NumberEditor(serverPort, "#"));
		details.setEditable(false);
		globalLog.setEditable(false);
		
		System.setErr(new PrintStream(
			new OutputStream()
			{
				@Override
				public void write(int b) throws IOException
				{
					globalLog.append(new String(new int[] {b}, 0, 1));
				}	
			}
		));
		
		serverRunning.addItemListener(this);
		clientConnected.addItemListener(this);
		
		serverEventList.addListSelectionListener(this);
		clientEventList.addListSelectionListener(this);
		
		pack();
	}
	
	private static class EventLog
	{
		private final String summary;
		private final String details;
		
		public EventLog(String summary, String details)
		{
			this.summary = summary;
			this.details = details;
		}
		
		@Override
		public String toString()
		{
			return summary;
		}
		
		public String getDetails()
		{
			return details;
		}
	}
	
	private static class GenericActionListener implements ngon.game.action.ActionListener
	{
		private final String prefix;
		private final DefaultListModel<EventLog> log;
		
		public GenericActionListener(String prefix, JList<EventLog> list)
		{
			this.prefix = prefix;
			log = (DefaultListModel<EventLog>)list.getModel();
		}

		@Override
		public void handleAction(ActionProducer source, Action act)
		{
			log.addElement(new EventLog(prefix + act.toString(), "From: " + source.toString() + "\n" + act.debugDetails()));
		}
	}

	protected void startServer()
	{
		if(serverServer != null)
			throw new IllegalStateException("Server is already started!");
		
		try
		{
			serverServer = new ngon.game.Server(serverName.getText(), (Integer)serverPort.getValue());
			serverServer.addActionListener(new GenericActionListener(serverName.getText() + ": ", serverEventList));
			log(serverEventList, "Server started.", "No details.");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void log(JList<EventLog> list, String summary, String details)
	{
		DefaultListModel<EventLog> log = (DefaultListModel<EventLog>)list.getModel();
		log.addElement(new EventLog(summary, details));
	}
	
	protected void stopServer()
	{
		serverServer.stop();
		serverServer = null;
		log(serverEventList, "Server stopped.", "No details.");
	}
	
	protected void startClient()
	{
		// TODO
		if(clientClient != null)
			throw new IllegalStateException("Client is already started!");

		try
		{
			clientClient = new ngon.game.Client(clientHostName.getText(), (Integer)clientHostPort.getValue(), clientName.getText());
			clientClient.addActionListener(new GenericActionListener(clientHostName.getText() + ": ", clientEventList));
			log(clientEventList, "Client connected.", "No details.");
		}
		catch(IOException e)
		{
			// TODO
			e.printStackTrace();
		}
	}
	
	protected void stopClient()
	{
		clientClient.disconnect();
		clientClient = null;
		log(clientEventList, "Client stopped.", "No details.");
	}

	public void actionPerformed(ActionEvent e)
	{
		// TODO
	}

	public void stateChanged(ChangeEvent e)
	{
		// TODO
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		// TODO Auto-generated method stub
		if(serverRunning.equals(e.getSource()))
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
				startServer();
			else
				stopServer();
		}
		else if(clientConnected.equals(e.getSource()))
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
				startClient();
			else
				stopClient();
		}
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if(serverEventList.equals(e.getSource()))
			details.setText(serverEventList.getSelectedValue().getDetails());
		else if(clientEventList.equals(e.getSource()))
			details.setText(clientEventList.getSelectedValue().getDetails());
	}
}
