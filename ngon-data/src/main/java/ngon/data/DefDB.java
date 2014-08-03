package ngon.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ngon.data.def.AbstractDefinition;

public class DefDB extends HashMap<Class<? extends AbstractDefinition>, Map<UUID, ? extends AbstractDefinition>> {
	@SuppressWarnings("unchecked")
	public <T extends AbstractDefinition> Map<UUID, T> get(Class<T> type)
	{
		if(!containsKey(type))
			super.put(type, new HashMap<UUID, T>());

		return (Map<UUID, T>) super.get(type);
	}

	public <T extends AbstractDefinition> T get(Class<T> type, UUID id)
	{
		return get(type).get(id);
	}
	
	public <T extends AbstractDefinition> void put(Class<T> type, UUID id, T object)
	{
		if(get(type) == null)
			put(type, new HashMap<UUID, T>());

		get(type).put(id, object);
	}

	public Map<UUID, ? extends AbstractDefinition> put(Class<? extends AbstractDefinition> type, Map<UUID, ? extends AbstractDefinition> map)
	{
		throw new UnsupportedOperationException("Sub-maps are managed internally.");
	}
}