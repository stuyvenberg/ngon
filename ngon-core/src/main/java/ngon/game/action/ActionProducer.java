package ngon.game.action;

import java.util.LinkedList;
import java.util.List;

public interface ActionProducer
{
	public void addActionListener(ActionListener listener);
	public boolean removeActionListener(ActionListener listener);
	
	public static class Simple implements ActionProducer
	{
		private List<ActionListener> listeners = new LinkedList<ActionListener>();
		
		public void addActionListener(ActionListener l)
		{
			if(!listeners.contains(l))
				listeners.add(l);
		}
		
		public boolean removeActionListener(ActionListener l)
		{
			return listeners.remove(l);
		}
		
		protected void dispatchAction(Action a)
		{
			dispatchAction(this, a);
		}

		protected void dispatchAction(ActionProducer src, Action a)
		{
			for(ActionListener l : listeners)
				l.handleAction(src, a);
		}
	}
}
