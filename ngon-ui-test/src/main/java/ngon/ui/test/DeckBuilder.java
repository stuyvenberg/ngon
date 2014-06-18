package ngon.ui.test;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import ngon.data.Database;
import ngon.data.def.CardDef;
import ngon.data.def.GameDef;
import ngon.data.def.ZoneDef;
import ngon.util.array.ArrayTools;
import ngon.util.array.ConcatenatingIterable;
import ngon.util.array.FilteringIterable;
import ngon.util.functions.Predicates;
import ngon.util.functions.Predicates.Predicate;

public class DeckBuilder extends JDialog implements ListSelectionListener
{
	private static final Icon DEFAULT_IMAGE = new ImageIcon("default-card-image.jpg");

	private static final Predicate<ZoneDef> loadable = new Predicate<ZoneDef>()
	{
		public boolean call(final ZoneDef on)
		{
			return on.properties.containsKey("loadable") && on.properties.get("loadable").equals(Boolean.TRUE);
		}
	};

	private static CardTableModel model(JTable table)
	{
		return (CardTableModel) table.getModel();
	}

	private class SwitchGameAction extends AbstractAction
	{
		public final GameDef definition;

		public SwitchGameAction(final GameDef def)
		{
			super(def.name);
			definition = def;
		}

		public void actionPerformed(ActionEvent e)
		{
			DeckBuilder.this.setGameDefinition(definition);
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
	private final Map<ZoneDef, JList<CardDef>> cardLists;
	private final JTable searchResults;

	private GameDef activeDef;

	protected static class CardTableModel extends AbstractTableModel
	{
		private final Database db;
		private final GameDef definition;
		private final List<String> columnNames;
		private final List<CardDef> matches;

		public CardTableModel(Database db, GameDef def)
		{
			this.db = db;
			this.definition = def;

			this.columnNames = new LinkedList<String>();
			this.matches = ArrayTools.listFromIterable(db.cards(definition));
			fireTableStructureChanged();

			setExtraColumns((List<String>) def.properties.get("default-properties"));
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

		public void search(Predicates.Predicate<CardDef>... conditions)
		{
			matches.clear();
			matches.addAll(ArrayTools.listFromIterable(db.cards(definition, new Predicates.All<CardDef>(conditions))));
			fireTableDataChanged();
		}

		public CardDef getRow(int idx)
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
				return matches.get(rowIndex).parentSet.name;
			case 1:
				return matches.get(rowIndex).name;
			default:
				return matches.get(rowIndex).properties.get(columnNames.get(columnIndex - 2));
			}
		}

	}

	protected void setGameDefinition(GameDef definition)
	{
		if (definition.equals(activeDef))
			return;

		CardDef proto = definition.cardPrototype;

		// Tear down the old information.
		cardLists.clear();

		while (zonePanes.getTabCount() > 0)
			zonePanes.removeTabAt(0);

		columns.removeAll();

		// Build up the new one.
		searchResults.setModel(new CardTableModel(database, definition));

		ImageIcon ico = new ImageIcon(proto.imageBack.toString());
		ico.getImage().flush();
		cardPreview.setIcon(ico.getImageLoadStatus() == MediaTracker.COMPLETE ? ico : DEFAULT_IMAGE);

		//@formatter:off
		Iterable<ZoneDef> zones = new ConcatenatingIterable<ZoneDef>(
			new FilteringIterable<ZoneDef>(definition.playerZones, loadable),
			new FilteringIterable<ZoneDef>(definition.sharedZones, loadable)
		);
		//@formatter:on

		for (ZoneDef def : zones)
		{
			JList<CardDef> list = new JList<CardDef>();
			cardLists.put(def, list);

			zonePanes.addTab(def.toString(), new JScrollPane(list));
		}

		for (String key : definition.cardPrototype.properties.keySet())
			columns.add(new JCheckBoxMenuItem(new EnableColumnAction(key)));

		// Initialize the search results with 'all'. Well, mostly -- large card libraries would obviously suffer here.

		activeDef = definition;
	}

	public DeckBuilder(Frame parent, Database db)
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

		for (GameDef def : db.games())
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

		cardLists = new HashMap<ZoneDef, JList<CardDef>>();

		gameMenu.getItem(0).doClick();

		pack();

		searchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		searchResults.setShowHorizontalLines(true);
		searchResults.setShowVerticalLines(true);
		searchResults.getSelectionModel().addListSelectionListener(this);
	}

	@Override
	public void valueChanged(ListSelectionEvent lse)
	{
		ImageIcon icon = new ImageIcon(
			(searchResults.getSelectedRow() < 0 ?
				activeDef.cardPrototype.imageBack :
				model(searchResults).getRow(searchResults.getSelectedRow()).imageFront
			).toString()
		);
		icon.getImage().flush();
		cardPreview.setIcon(icon);
	}
}
