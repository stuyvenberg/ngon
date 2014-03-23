package ngon.util.xml;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ElementListIterable implements Iterable<Element>
{
	private class ElementListIterator implements Iterator<Element>
	{
		private final NodeList list;
		private int i;
		
		public ElementListIterator(NodeList nodes)
		{
			this.i = 0;
			this.list = nodes;
		}

		@Override
		public boolean hasNext()
		{
			return this.i < this.list.getLength();
		}

		@Override
		public Element next()
		{
			return (Element) this.list.item(i++);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
	}
	
	private final NodeList list;
	
	public ElementListIterable(NodeList list)
	{
		this.list = list;
	}

	@Override
	public Iterator<Element> iterator()
	{
		// TODO Auto-generated method stub
		return new ElementListIterator(list);
	}

}
