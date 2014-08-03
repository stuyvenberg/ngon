package ngon.ui.test;

import java.awt.Frame;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import ngon.data.database.Database;
import ngon.data.database.Database.DatabaseException;
import ngon.data.def.CardDefinition;
import ngon.data.def.GameDefinition;
import ngon.data.def.ZoneDefinition;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DeckBuilder extends JDialog implements ListSelectionListener
{
	private static final Icon DEFAULT_IMAGE = new ImageIcon("default-card-image.jpg");

	private static CardTableModel model(JTable table)
	{
		return (CardTableModel) table.getModel();
	}

	private class SwitchGameAction extends AbstractAction
	{
		public final GameDefinition definition;

		public SwitchGameAction(final GameDefinition def)
		{
			super(def.toString());
			definition = def;
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				DeckBuilder.this.setGameDefinition(definition);
			}
			catch (IOException ioe)
			{
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			}
			catch (DatabaseException dbe)
			{
				// TODO Auto-generated catch block
				dbe.printStackTrace();
			}
		}
	}

	private final Action newDeckAction = new AbstractAction("New")
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{

		}
	};

	private final Action loadDeckAction = new AbstractAction("Load")
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{

		}
	};

	private final Action saveDeckAction = new AbstractAction("Save")
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{

		}
	};

	private final Action closeAction = new AbstractAction("Close")
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{

		}
	};

	private class EnableColumnAction extends AbstractAction
	{
		public EnableColumnAction(String col)
		{
			super(col);
			putValue(Action.SELECTED_KEY, model(searchResults).columnEnabled(col));
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			model(searchResults).enableColumn((String) getValue(Action.NAME), (Boolean) getValue(Action.SELECTED_KEY));
			DeckBuilder.this.pack();
		}
	}

	private final Database database;
	private final JMenu columns;
	private final JLabel cardPreview;
	private final JTabbedPane zonePanes;
	private final Map<ZoneDefinition, JList<CardDefinition>> cardLists;
	private final JTable searchResults;

	private GameDefinition activeDef;

	protected static class CardTableModel extends AbstractTableModel
	{
		private final Database db;
		private final GameDefinition definition;
		private final List<String> columnNames;
		private final List<CardDefinition> matches;

		public CardTableModel(Database db, GameDefinition def) throws IOException, DatabaseException
		{
			this.db = db;
			this.definition = def;

			this.columnNames = new LinkedList<String>();
			this.matches = Lists.newLinkedList(db.cards(definition));
			fireTableStructureChanged();

			setExtraColumns(def.getProperty("default-properties", new String[] {}));
		}

		public void setExtraColumns(String... columns)
		{
			setExtraColumns(Arrays.asList(columns));
		}

		public void setExtraColumns(Iterable<String> columns)
		{
			columnNames.clear();

			for (String col : columns)
				columnNames.add(col);

			fireTableStructureChanged();
		}

		public void enableColumn(String column, boolean on)
		{
			if (on)
				columnNames.add(column);
			else
				while (columnNames.remove(column))
					;

			fireTableStructureChanged();
		}
		
		public boolean columnEnabled(String column)
		{
			return columnNames.contains(column);
		}

		public void search(Predicate<CardDefinition>... conditions) throws IOException, DatabaseException
		{
			matches.clear();
			matches.addAll(Lists.newLinkedList(Iterables.filter(db.cards(definition), Predicates.and(conditions))));
			fireTableDataChanged();
		}

		public CardDefinition getRow(int idx)
		{
			return matches.get(idx);
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.size() + 2;
		}

		@Override
		public int getRowCount()
		{
			return matches.size();
		}

		@Override
		public String getColumnName(int index)
		{
			switch (index)
			{
			case 0:
				return "Set";
			case 1:
				return "Name";
			default:
				return columnNames.get(index - 2);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			switch (columnIndex)
			{
			case 0:
//				return matches.get(rowIndex).getSet().toString();
				return "(???)";
			case 1:
				return matches.get(rowIndex).toString();
			default:
				return matches.get(rowIndex).getProperty(columnNames.get(columnIndex - 2), "(no value)");
			}
		}

	}

	protected void setGameDefinition(final GameDefinition definition) throws IOException, DatabaseException
	{
		if (definition.equals(activeDef))
			return;

		CardDefinition proto = definition.getCardPrototype();

		// Tear down the old information.
		cardLists.clear();

		while (zonePanes.getTabCount() > 0)
			zonePanes.removeTabAt(0);

		columns.removeAll();

		// Build up the new one.
		searchResults.setModel(new CardTableModel(database, definition));

		ImageIcon ico = new ImageIcon(ImageIO.read(proto.getImageStream()));
		ico.getImage().flush();
		cardPreview.setIcon(ico.getImageLoadStatus() == MediaTracker.COMPLETE ? ico : DEFAULT_IMAGE);

		//@formatter:off
		Iterable<ZoneDefinition> zones = Iterables.concat(
			Iterables.filter(definition.instancedZones(), zone -> zone.getProperty("loadable", false)),
			Iterables.filter(definition.sharedZones(), zone -> zone.getProperty("loadable", false))
		);
		//@formatter:on

		for (ZoneDefinition def : zones)
		{
			JList<CardDefinition> list = new JList<CardDefinition>();
			cardLists.put(def, list);

			zonePanes.addTab(def.toString(), new JScrollPane(list));
		}

		for (String key : proto.getPropertyNames())
			columns.add(new JCheckBoxMenuItem(new EnableColumnAction(key)));

		// Initialize the search results with 'all'. Well, mostly -- large card libraries would obviously suffer here.

		activeDef = definition;
	}

	public DeckBuilder(Frame parent, Database db) throws IOException, DatabaseException
	{
		super(parent, "ngon Test UI - Deck Builder");

		this.database = db;

		JMenu gameMenu;

		//@formatter:off
		this.setJMenuBar(LayoutUtils.menuBar(
			LayoutUtils.menu("Deck",
				new JMenuItem(newDeckAction),
				new JMenuItem(loadDeckAction),
				new JMenuItem(saveDeckAction),
				new JMenuItem(closeAction)
			),
			gameMenu = LayoutUtils.menu("Game"),
			columns = LayoutUtils.menu("Columns")
		));
		//@formatter:on

		for (GameDefinition def : db.games())
			gameMenu.add(new JRadioButtonMenuItem(new SwitchGameAction(def)));

		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		//@formatter:off
		LayoutUtils.addAll(getContentPane(),
			LayoutUtils.vert(
				cardPreview = new JLabel(DEFAULT_IMAGE),
				LayoutUtils.titled("Game Zones", new JScrollPane(zonePanes = new JTabbedPane()))
			),
			LayoutUtils.vert(
				LayoutUtils.horiz("Card Search",
					new JLabel("Test")
				),
				LayoutUtils.horiz("Matching Cards", new JScrollPane(searchResults = new JTable()))
			)
		);
		//@formatter:on

//		cardPreview.setMinimumSize(cardPreview.getPreferredSize());
//		cardPreview.setMaximumSize(cardPreview.getPreferredSize());

		cardLists = new HashMap<ZoneDefinition, JList<CardDefinition>>();

		gameMenu.getItem(0).doClick();

		searchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		searchResults.setShowHorizontalLines(true);
		searchResults.setShowVerticalLines(true);
		searchResults.getSelectionModel().addListSelectionListener(this);

		searchResults.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		pack();
	}

	@Override
	public void valueChanged(ListSelectionEvent lse)
	{
		ImageIcon icon;
		try
		{
			icon = new ImageIcon(ImageIO.read(
				(searchResults.getSelectedRow() < 0 ?
					activeDef.getCardPrototype().getImageStream() :
					model(searchResults).getRow(searchResults.getSelectedRow()).getImageStream()
				)
			));

			icon.getImage().flush();
			cardPreview.setIcon(icon);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
