package ngon.game.obj;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class GameObject implements Serializable
{
	private static Map<UUID, GameObject> objectMap = new Hashtable<UUID, GameObject>();
	
	public final UUID id;
	protected final RefUUID rid;
	
	public GameObject(UUID id)
	{
		this.id = id;
		this.rid = new RefUUID(this.id);
		
		objectMap.put(id, this);
	}
	
	public Object writeReplace()
	{
		return rid;
	}
	
	public void dispose()
	{
		objectMap.remove(this.id);
	}
	
	public static class RefUUID implements Serializable
	{
		public final UUID id;
		
		public RefUUID(UUID id)
		{
			this.id = id;
		}
		
		public Object readResolve()
		{
			return objectMap.get(this.id);
		}
	}
}
