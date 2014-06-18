package ngon.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NonGameObjRefUUID
{
	public final UUID id;
	
	public NonGameObjRefUUID(String id, Object ref)
	{
		this.id = UUID.fromString(id);
		referenceMap.put(this, ref);
	}
	
	public NonGameObjRefUUID(UUID id, Object ref)
	{
		this.id = id;
		referenceMap.put(this, ref);
	}
	
	private static Map<NonGameObjRefUUID, Object> referenceMap = new HashMap<NonGameObjRefUUID, Object>();
	
	private Object readResolve()
	{
		return referenceMap.get(this);
	}
}
