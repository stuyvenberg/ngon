package ngon.game.def;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ngon.util.xml.ElementListIterable;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DefinitionProperties extends HashMap<String, Object>
{
	private static final long serialVersionUID = 7265526861185693014L;

	private NodeList list;
	private ElementListIterable iterList;
	
	public DefinitionProperties(Element xmlel)
	{
		super();
		
		list = xmlel.getElementsByTagName("property");
		iterList = new ElementListIterable(list);
	}
	
	public DefinitionProperties(Object... kvs)
	{
		if(kvs.length % 2 != 0)
			throw new IllegalArgumentException("kvs not in key/value form (odd number of elements)");
		
		for(int i = 0; i < (kvs.length % 2 == 0 ? kvs.length : kvs.length - 1); i += 2)
			super.put(kvs[i].toString(), kvs[i+1]);
		
		list = null;
		iterList = null;
	}
	
	@Override
	public Object clone()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object arg0)
	{
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key)
	{
		if(super.containsKey(key))
			return true;
		
		if(iterList == null)
			return false;

		for(Element el : iterList)
		{
			if(key.equals(el.getAttribute("name")))
			{
				Object val = parseValue(el);
				super.put((String) key, val);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean containsValue(Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key)
	{
		if(super.containsKey(key))
			return super.get(key);
		
		if(iterList == null)
			return null;
		
		for(Element el : iterList)
		{
			if(key.equals(el.getAttribute("name")))
			{
				Object val = parseValue(el);
				super.put((String) key, val);
				return val;
			}
		}

		return null;
	}

	@Override
	public boolean isEmpty()
	{
		if(!super.isEmpty())
			return false;
		
		return list == null ? true : list.getLength() == 0;
	}

	@Override
	public Object put(String arg0, Object arg1)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> arg0)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object arg0)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size()
	{
		return (list == null ? super.size() : list.getLength());
	}

	@Override
	public Collection<Object> values()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private static Object parseValue(Element prop)
	{
		switch(prop.getAttribute("type"))
		{
			case "integer":
				return Integer.parseInt(prop.getAttribute("value"));
			case "number":
				return Double.parseDouble(prop.getAttribute("value"));
			case "string":
			default:
				return prop.getAttribute("value");
		}
	}
}
