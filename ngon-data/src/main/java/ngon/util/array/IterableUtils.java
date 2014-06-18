package ngon.util.array;

import ngon.util.functions.Maps;
import ngon.util.functions.Predicates;

public class IterableUtils
{
	/**
	 * Filters the given iterable with the given predicate, then casts to the destination type. This call should be totally safe.
	 * 
	 * @param source
	 *            The source iterable to filter/cast.
	 * @param pred
	 *            The filter condition.
	 * @return An iterable that filters and casts the source iterable to the desired result type.
	 */
	public static <U extends V, V> Iterable<V> filterAndCast(final Iterable<U> source, final Predicates.Predicate<U> pred)
	{
		return filterAndCastUnchecked(source, pred);
	}

	/**
	 * Filters the given iterable with the given predicate, then casts to the destination type. Warning: The cast is unchecked; the caller is responsible for ensuring the types can be safely cast (i.e. using instanceof).
	 * 
	 * @param source
	 *            The source iterable to filter/cast.
	 * @param pred
	 *            The filter condition.
	 * @return An iterable that filters and casts the source iterable to the desired result type.
	 */
	public static <U, V extends U> Iterable<V> filterAndUpcast(final Iterable<U> source, final Predicates.Predicate<U> pred)
	{
		return filterAndCastUnchecked(source, pred);
	}

	private static <U, V> Iterable<V> filterAndCastUnchecked(final Iterable<U> source, final Predicates.Predicate<U> pred)
	{
		//@formatter:off
		return new MappingIterable<>(
			new FilteringIterable<>(source, pred),
			new Maps.Map<U, V>()
			{
				@SuppressWarnings("unchecked")
				public V call(U on)
				{
					return (V) on;
				}
			}
		);
		//@formatter:on
	}

	public static <U extends V, V> Iterable<V> cast(Iterable<U> in)
	{
		return new MappingIterable<U, V>(in, new Maps.Cast<U, V>());
	}
	
	public static <U, V> Iterable<V> unsafeCast(Iterable<U> in)
	{
		return new MappingIterable<U, V>(in, new Maps.UnsafeCast<U, V>());
	}
}
