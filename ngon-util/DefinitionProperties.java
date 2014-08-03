package ngon.data.def;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ngon.util.array.IterableUtils;
import ngon.util.functions.Predicates;
import ngon.util.functions.Predicates.Predicate;
import ngon.util.xml.NodeListIterable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DefinitionProperties extends HashMap<String, Object>
{
	private static final long serialVersionUID = 7265526861185693014L;

	private static final Predicate<Node> elprop = new Predicate<Node>()
	{
		public boolean call(Node on)
		{
			return on.getNodeType() == Node.ELEMENT_NODE && ((Element) on).getTagName().equals("property");
		}
	};

	private final Iterator<Element> iter;

	public DefinitionProperties(Element xmlel)
	{
		super();

		Iterable<Element> iterable = IterableUtils.filterAndUpcast(new NodeListIterable(xmlel.getChildNodes()), elprop);
		iter = iterable.iterator();
	}

	public DefinitionProperties(Object... kvs)
	{
		if (kvs.length % 2 != 0)
			throw new IllegalArgumentException("kvs not in key/value form (odd number of elements)");

		for (int i = 0; i < (kvs.length % 2 == 0 ? kvs.length : kvs.length - 1); i += 2)
			super.put(kvs[i].toString(), kvs[i + 1]);

		iter = null;
	}

	private Element drainUntil(Predicates.Predicate<Element> condition)
	{
		while (iter.hasNext())
		{
			Element el = iter.next();
			Object val = parseValue(el);
			super.put(el.getAttribute("name"), val);

			if (condition.call(el))
				return el;
		}

		return null;
	}

	private void drainAll()
	{
		drainUntil(new Predicates.Predicate<Element>()
		{
			public boolean call(Element on)
			{
				return false;
			}
		});
	}

	@Override
	public Object clone()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		drainAll();

		return super.entrySet();
	}

	@Override
	public Set<String> keySet()
	{
		drainAll();

		return super.keySet();
	}

	@Override
	public boolean equals(Object arg0)
	{
		return super.equals(arg0);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	@Override
	public String toString()
	{
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
		if (super.containsKey(key))
			return true;

		if (iter == null || !iter.hasNext())
			return false;

		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value)
	{
		if (super.containsValue(value))
			return true;

		if (iter == null || !iter.hasNext())
			return false;

		drainAll();
		return containsValue(value);
	}

	@Override
	public Object get(final Object key)
	{
		if (super.containsKey(key))
			return super.get(key);

		if (iter == null || !iter.hasNext())
			return null;

		if (drainUntil(new Predicates.Predicate<Element>()
		{
			public boolean call(Element el)
			{
				return key.equals(el.getAttribute("name"));
			}
		}) != null)
		{
			return super.get(key);
		}

		return null;
	}

	@Override
	public boolean isEmpty()
	{
		if (!super.isEmpty())
			return false;

		return iter.hasNext();
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
		drainAll();

		return super.size();
	}

	@Override
	public Collection<Object> values()
	{
		drainAll();

		return super.values();
	}

	private static enum PropType
	{
		TEXT
		{
			public Object parse(String text)
			{
				return text;
			}
		},

		CDATA
		{
			public Object parse(String text)
			{
				return text;
			}
		},

		NUMBER
		{
			public Object parse(String text)
			{
				return Double.parseDouble(text);
			}
		},

		INTEGER
		{
			public Object parse(String text)
			{
				return Integer.parseInt(text);
			}
		},

		BOOL
		{
			public Object parse(String text)
			{
				return Boolean.parseBoolean(text);
			}
		};

		public abstract Object parse(String text);
	}

	private static Object parseValue(Element prop)
	{
		return (prop.hasAttribute("type") ? PropType.valueOf(prop.getAttribute("type").toUpperCase()) : PropType.TEXT).parse((prop.hasAttribute("value") ? prop.getAttribute("value") : prop.getTextContent()));
	}
}
