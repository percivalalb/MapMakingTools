package mapmakingtools.common.killentities;

import net.minecraft.entity.Entity;
import mapmakingtools.api.IForceKill;

/**
 * @author ProPercivalalb
 */
public class KillAll implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity != null;
	}
}
