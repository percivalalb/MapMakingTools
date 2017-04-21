package mapmakingtools.tools.killentities;

import mapmakingtools.api.interfaces.IForceKill;
import net.minecraft.entity.Entity;

/**
 * @author ProPercivalalb
 */
public class KillAll implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity != null;
	}
}
