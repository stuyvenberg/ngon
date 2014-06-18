package ngon.data.def;

/**
 * Something is wrong with a definition. Take a look at the message and fix it, silly!
 */
public class DefinitionException extends Exception
{

	public DefinitionException(String string)
	{
		super(string);
	}

}
