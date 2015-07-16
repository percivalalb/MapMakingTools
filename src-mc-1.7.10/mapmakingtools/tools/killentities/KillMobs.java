package mapmakingtools.tools.killentities;

import mapmakingtools.api.interfaces.IForceKill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;

/**
 * @author ProPercivalalb
 */
public class KillMobs implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity instanceof EntityMob;
	}
}
