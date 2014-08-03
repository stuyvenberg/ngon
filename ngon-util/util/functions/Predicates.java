package ngon.util.functions;

public class Predicates
{
	public static interface Predicate<T>
	{
		public boolean call(final T object);
	}
	
	public static class All<T> implements Predicate<T>
	{
		private final Predicate<T>[] preds;
		
		@SafeVarargs
		public All(Predicate<T>... preds)
		{
			this.preds = preds;
		}
		
		public boolean call(T on) {
			for(Predicate<T> pred : preds)
				if(!pred.call(on))
					return false;
			
			return true;
		}
	}

	public static class Any<T> implements Predicate<T>
	{
		private final Predicate<T>[] preds;
		
		@SafeVarargs
		public Any(Predicate<T>... preds)
		{
			this.preds = preds;
		}
		
		public boolean call(T on) {
			for(Predicate<T> pred : preds)
				if(pred.call(on))
					return true;
			
			return false;
		}
	}}
