package mapmakingtools.tools.killentities;

import mapmakingtools.api.interfaces.IForceKill;
import net.minecraft.entity.Entity;

/**
 * @author ProPercivalalb
 */
public class KillGeneric implements IForceKill {

	private final Class<? extends Entity> entityClass;
	
	public KillGeneric(Class<? extends Entity> entityClass) {
		this.entityClass = entityClass;
	}
	
	@Override
	public boolean onCommand(Entity entity) {
		if(entity == null)
			return false;
		return entity.getClass().equals(entityClass);
	}
}
