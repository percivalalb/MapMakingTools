package mapmakingtools.common.killentities;

import net.minecraft.entity.Entity;
import mapmakingtools.api.IForceKill;

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
