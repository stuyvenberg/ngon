package ngon.util.array;

import java.util.Iterator;

public class ArrayIterable<T> implements Iterable<T>
{
	public static class ArrayIterator<T> implements Iterator<T>
	{
		private final T[] source;
		private int index;
	
		public ArrayIterator(T[] array)
		{
			source = array;
			index = 0;
		}
	
		public boolean hasNext()
		{
			return index < source.length;
		}
	
		public T next()
		{
			return source[index++];
		}
	
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private final T[] source;
	
	public ArrayIterable(T[] source)
	{
		this.source = source;
	}
	
	public Iterator<T> iterator()
	{
		return new ArrayIterator<T>(source);
	}
}