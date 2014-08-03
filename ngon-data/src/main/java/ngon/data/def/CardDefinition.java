package ngon.data.def;

import java.io.IOException;
import java.io.InputStream;

public interface CardDefinition extends Definition {
	public InputStream getImageStream() throws IOException;
}
