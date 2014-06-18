package ngon.util.functions;

public class Maps
{
	public static interface Map<U, V>
	{
		public V call(final U on);
	}

	public static class ComposedMap<T, U, V> implements Map<T, V>
	{
		private final Map<T, U> a;
		private final Map<U, V> b;

		public ComposedMap(Map<T, U> a, Map<U, V> b)
		{
			this.a = a;
			this.b = b;
		}

		public V call(T on)
		{
			return b.call(a.call(on));
		}
	}

	public static class MultiComposedMap<T, V> implements Map<T, V>
	{
		private final Map<T, Object> begin;
		private final Map<Object, Object>[] between;
		private final Map<Object, V> end;

		@SafeVarargs
		public MultiComposedMap(Map<T, Object> begin, Map<Object, V> end, Map<Object, Object>... between)
		{
			this.begin = begin;
			this.between = between;
			this.end = end;
		}

		public V call(T on)
		{
			Object p = begin.call(on);

			for (Map<Object, Object> m : between)
				p = m.call(p);

			return end.call(p);
		}
	}

	public static class Cast<U extends V, V> implements Map<U, V>
	{
		public V call(U on)
		{
			return (V) on;
		}
	}
	
	public static class UnsafeCast<U, V> implements Map<U, V>
	{
		@SuppressWarnings("unchecked")
		public V call(U on)
		{
			return (V) on;
		}
	}
}
