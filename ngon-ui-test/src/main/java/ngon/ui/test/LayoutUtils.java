package ngon.ui.test;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class LayoutUtils
{
	public static JPanel form(Object... elements)
	{
		JPanel box = new JPanel();
		box.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		GroupLayout layout = new GroupLayout(box); 
		box.setLayout(layout);
		
		SequentialGroup vert = layout.createSequentialGroup();
		ParallelGroup labels = layout.createParallelGroup();
		ParallelGroup components = layout.createParallelGroup();
		
		for(int i=0; i < elements.length; i += 2)
		{
			Component label = toComponent(elements[i]);
			Component comp = toComponent(elements[i + 1]);
			
			vert.addGroup(
				layout.createParallelGroup()
					.addComponent(label, Alignment.CENTER)
					.addComponent(comp, Alignment.CENTER)
			)
			.addGap(8);
			
			labels.addComponent(label, Alignment.TRAILING);
			components.addComponent(comp);
		}
		
		layout.setVerticalGroup(vert);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(labels)
			.addGap(8)
			.addGroup(components)
		);
		
		return box;
	}
	
	public static <T extends JComponent> T titled(String title, T object)
	{
		object.setBorder(BorderFactory.createTitledBorder(title));
		
		return object;
	}
	
	public static <T extends Container> T addAll(T to, Component... cs)
	{
		for(Component c : cs)
			to.add(c);
		
		return to;
	}
	
	public static Box horiz(Component... cs)
	{
		return addAll(Box.createHorizontalBox(), cs);
	}
	
	public static Box horiz(String title, Component... cs)
	{
		return titled(title, horiz(cs));
	}
	
	public static Box vert(Component... cs)
	{
		return addAll(Box.createVerticalBox(), cs);
	}
	
	public static Box vert(String title, Component... cs)
	{
		return titled(title, vert(cs));
	}
	
	public static JPanel column(Component... cs)
	{
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setVerticalGroup(addAll(layout.createSequentialGroup(), cs));
		layout.setHorizontalGroup(addAll(layout.createParallelGroup(), cs));

		return panel;
	}
	
	public static JPanel row(Component... cs)
	{
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setVerticalGroup(addAll(layout.createParallelGroup(), cs));
		layout.setHorizontalGroup(addAll(layout.createSequentialGroup(), cs));

		return panel;
	}

	private static <T extends Group> T addAll(T in, Component... cs)
	{
		for(Component c : cs)
			in.addComponent(c);
		
		return in;
	}
	
	private static Component toComponent(Object obj)
	{
		return (obj instanceof Component ? (Component)obj : new JLabel(obj.toString()));
	}
	
	public static JMenuBar menuBar(JMenuItem... items)
	{
		JMenuBar bar = new JMenuBar();
		addAll(bar, items);
		return bar;
	}
	
	public static JMenu menu(String name, JMenuItem... items)
	{
		JMenu menu = new JMenu(name);
		addAll(menu, items);
		return menu;
	}
}
