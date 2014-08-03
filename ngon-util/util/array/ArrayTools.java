package ngon.util.array;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import ngon.util.functions.Predicates;

public class ArrayTools
{
	@SuppressWarnings("unchecked")
	public static <T> T[] arrayFromIterator(Iterator<T> i)
	{
		return (T[]) listFromIterator(i).toArray();
	}
	
	public static <T> T[] arrayFromIterable(Iterable<T> i)
	{
		return arrayFromIterator(i.iterator());
	}
	
	public static <T> List<T> listFromIterable(Iterable<T> i)
	{
		return listFromIterator(i.iterator());
	}

	public static <T> List<T> listFromIterator(Iterator<T> i)
	{
		List<T> out = new LinkedList<T>();

		while (i.hasNext())
			out.add(i.next());

		return out;
	}

	public static <T> T[] filter(T[] original, Predicates.Predicate<T> condition)
	{
		return arrayFromIterator(new FilteringIterable.FilteringIterator<T>(new ArrayIterable.ArrayIterator<T>(original), condition));
	}
	
	public static <T> T[] concat(T[] a, T[] b)
	{
		T[] out = Arrays.copyOf(a, a.length + b.length);
		
		for(int i = a.length; i < out.length; ++i)
			out[i] = b[i - a.length];
		
		return out;
	}
}
