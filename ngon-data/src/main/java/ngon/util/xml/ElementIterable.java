package ngon.util.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;

public class ElementIterable implements Iterable<Element>
{
	private static Element nextElement(Element of)
	{
		while(of != null)
		{
			org.w3c.dom.Node node = of.getNextSibling();
			while(node != null && node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
				node = node.getNextSibling();
			
			of = (Element) node;
		}
		
		return of;
	}

	public static ElementIterable children(Element of, String name, Object... attributePairs)
	{
		return new ElementIterable((Element) of.getFirstChild(), name, attributePairs);
	}
	
	public static ElementIterable children(Element of, String name)
	{
		return children(of, name, (Object[])null);
	}
	
	public static ElementIterable children(Element of)
	{
		return children(of, null, (Object[])null);
	}
	
	public static Element firstChild(Element of, String name, Object... attributes)
	{
		return children(of, name, attributes).iterator().next();
	}
	
	public static Element firstChild(Element of, String name)
	{
		return children(of, name, (Object[])null).iterator().next();
	}

	public static Element firstChild(Element of)
	{
		return children(of, null, (Object[])null).iterator().next();
	}

	private class FilteredElementIterator implements Iterator<Element>
	{
		private final String nameFilter;
		private final Map<String, Object> attributeFilters;
		
		private Element next;

		public FilteredElementIterator(Element first, String nameFilter, Object... attributeFilters)
		{
			this.next = first;
			
			while(next != null && !matches(next))
			{
				org.w3c.dom.Node node = next.getNextSibling();
				while(node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
					node = node.getNextSibling();
				
				next = (Element) node;
			}
			
			this.nameFilter = nameFilter;
			this.attributeFilters = new HashMap<String, Object>();
			for(int i=0; i < attributeFilters.length - 1; i += 2)
				this.attributeFilters.put(attributeFilters[i].toString(), attributeFilters[i+1]);
		}
		
		private boolean matches(Element against)
		{
			if(against == null)
				return false;
			
			if(nameFilter != null && !against.getTagName().equals(nameFilter))
				return false;
			
			if(attributeFilters == null)
				return true;
			
			boolean match = true;
			for(String key : attributeFilters.keySet())
			{
				if(!against.hasAttribute(key) || !against.getAttribute(key).equals(attributeFilters.get(key)))
				{
					match = false;
					break;
				}
			}

			return match;
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public Element next()
		{
			Element tmpNext = next;

			while(next != null && !matches(next = nextElement(next)));
			
			return tmpNext;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
	}
	
	private final Element first;
	private final String nameFilter;
	private final Object[] attributeFilters;
	
	public ElementIterable(Element first, String nameFilter, Object... attributeFilters)
	{
		this.first = first;
		
		this.nameFilter = nameFilter;
		this.attributeFilters = attributeFilters;
	}

	@Override
	public Iterator<Element> iterator()
	{
		// TODO Auto-generated method stub
		return new FilteredElementIterator(first, nameFilter, attributeFilters);
	}

}
