package ngon.util.array;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ngon.util.functions.Maps;

public class MappingIterable<U, V> implements Iterable<V>
{
	public static class MappingIterator<U, V> implements Iterator<V>
	{
		private final Iterator<U> source;
		private final Maps.Map<U, V> map;

		public MappingIterator(Iterator<U> source, Maps.Map<U, V> map)
		{
			this.source = source;
			this.map = map;
		}

		@Override
		public boolean hasNext()
		{
			return source.hasNext();
		}

		@Override
		public V next()
		{
			if(!hasNext())
				throw new NoSuchElementException();

			return map.call(source.next());
		}

		@Override
		public void remove()
		{
			source.remove();
		}
	}

	private final Maps.Map<U, V> map;
	private final Iterable<U> source;

	public MappingIterable(Iterable<U> source, Maps.Map<U, V> map)
	{
		this.source = source;
		this.map = map;
	}

	@Override
	public Iterator<V> iterator()
	{
		return new MappingIterator<U, V>(source.iterator(), map);
	}
}
