package ngon.util.array;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ngon.util.functions.Predicates.Predicate;

public class FilteringIterable<T> implements Iterable<T>
{
	public static class FilteringIterator<T> implements Iterator<T>
	{
		private final Predicate<T> condition;
		private final Iterator<T> source;
		private T next;

		public FilteringIterator(Iterator<T> source, Predicate<T> condition)
		{
			this.source = source;
			this.condition = condition;
			this.next = source.next();

			while (next != null && !condition.call(next))
				next = (source.hasNext() ? source.next() : null);
		}

		public boolean hasNext()
		{
			return next != null;
		}

		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			T old = next;
			next = (source.hasNext() ? source.next() : null);

			while (next != null && !condition.call(next))
				next = (source.hasNext() ? source.next() : null);

			return old;
		}

		public void remove()
		{
			// Even if the underlying iterator supports it, we're already too far -- by necessity.
			throw new UnsupportedOperationException();
		}
	}

	private final Iterable<T> source;
	private final Predicate<T> condition;

	public FilteringIterable(Iterable<T> source, Predicate<T> condition)
	{
		this.source = source;
		this.condition = condition;
	}

	public Iterator<T> iterator()
	{
		return new FilteringIterator<T>(source.iterator(), condition);
	}

}
