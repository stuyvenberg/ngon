package ngon.util.xml;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterable implements Iterable<Node>
{
	public static class NodeListIterator implements Iterator<Node>
	{
		private final NodeList list;
		private int i;

		public NodeListIterator(NodeList list)
		{
			this.list = list;
			this.i = 0;
		}

		public boolean hasNext()
		{
			return i < list.getLength();
		}

		public Node next()
		{
			return list.item(i++);
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private final NodeList list;

	public NodeListIterable(NodeList list)
	{
		this.list = list;
	}

	public Iterator<Node> iterator()
	{
		return new NodeListIterator(list);
	}
}
