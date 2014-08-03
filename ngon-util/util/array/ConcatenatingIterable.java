package ngon.util.array;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class ConcatenatingIterable<T> implements Iterable<T>
{
	public static class ConcatenatingIterator<T> implements Iterator<T>
	{
		private final Iterator<Iterator<T>> iteriter;
		private Iterator<T> current;

		public ConcatenatingIterator(Iterator<Iterator<T>> iteriter)
		{
			this.iteriter = iteriter;
			this.current = (iteriter.hasNext() ? iteriter.next() : null);
		}

		public boolean hasNext()
		{
			return current != null && current.hasNext();
		}

		public T next()
		{
			if (!hasNext())
				throw new NoSuchElementException();

			T next = current.next();
			while (current != null && !current.hasNext())
				current = (iteriter.hasNext() ? iteriter.next() : null);

			return next;
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}

	private final Iterable<Iterable<T>> list;

	public ConcatenatingIterable(Iterable<Iterable<T>> list)
	{
		this.list = list;
	}

	@SafeVarargs
	public ConcatenatingIterable(Iterable<T>... list)
	{
		this.list = new ArrayIterable<Iterable<T>>(list);
	}

	public Iterator<T> iterator()
	{
		List<Iterator<T>> iterList = new LinkedList<Iterator<T>>();

		for (Iterable<T> iter : list)
			iterList.add(iter.iterator());

		return new ConcatenatingIterator<T>(iterList.iterator());
	}

}
