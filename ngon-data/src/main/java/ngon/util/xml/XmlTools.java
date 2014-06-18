package ngon.util.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ngon.util.array.IterableUtils;
import ngon.util.functions.Predicates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XmlTools
{
	public static Document loadDocument(File path) throws IOException, SAXException, ParserConfigurationException
	{
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path);
	}

	public static Document loadDocument(String path) throws IOException, SAXException, ParserConfigurationException
	{
		return loadDocument(new File(path));
	}

	public static Predicates.Predicate<Node> namedElementPredicate(final String string)
	{
		return new Predicates.Predicate<Node>()
		{
			public boolean call(Node on)
			{
				return on.getNodeType() == Node.ELEMENT_NODE && ((Element) on).getTagName().equals(string);
			}
		};
	}

	public static class ElementFilter implements Predicates.Predicate<Node>
	{
		private final Map<String, String> attributeMap;
		private final String tagName;

		public ElementFilter(String tagName, Object... attributes)
		{
			if (attributes != null && attributes.length % 2 != 0)
				throw new IllegalArgumentException("ElementFilter expects an even number of keys and values.");

			this.tagName = tagName;

			if (attributes != null)
			{
				attributeMap = new HashMap<String, String>();
				for (int i = 0; i < attributes.length; /* null */)
					attributeMap.put(attributes[i++].toString(), attributes[i++].toString());
			}
			else
			{
				attributeMap = null;
			}
		}

		public boolean call(Node on)
		{
			if (on.getNodeType() != Node.ELEMENT_NODE)
				return false;

			Element el = (Element) on;

			if (tagName != null && !el.getTagName().equals(tagName))
				return false;

			if (attributeMap != null)
				for (Map.Entry<String, String> kvs : attributeMap.entrySet())
					if (!el.hasAttribute(kvs.getKey()) || !el.getAttribute(kvs.getKey()).equals(kvs.getValue()))
						return false;

			return true;
		}
	}

	public static Iterable<Element> childElements(Node of, ElementFilter filter)
	{
		return IterableUtils.filterAndUpcast(new NodeListIterable(of.getChildNodes()), filter);
	}

	public static Iterable<Element> childElements(Node of, String tagName, Object... attributes)
	{
		return childElements(of, new ElementFilter(tagName, attributes));
	};

	public static Iterable<Element> childElements(Node of, Object... tagNameOrAttributes)
	{
		//@formatter:off
		if (tagNameOrAttributes.length == 0)
			return childElements(of, new ElementFilter(null, (Object[]) null));
		else if (tagNameOrAttributes.length == 1)
			return childElements(of, new ElementFilter(tagNameOrAttributes[0].toString(), (Object[]) null));
		else
			return childElements(of, new ElementFilter(null, tagNameOrAttributes));
		//@formatter:on
	};
}
