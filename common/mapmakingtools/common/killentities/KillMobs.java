package mapmakingtools.common.killentities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import mapmakingtools.api.IForceKill;

/**
 * @author ProPercivalalb
 */
public class KillMobs implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity instanceof EntityMob;
	}
}
