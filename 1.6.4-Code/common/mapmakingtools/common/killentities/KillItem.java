package mapmakingtools.common.killentities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import mapmakingtools.api.IForceKill;

/**
 * @author ProPercivalalb
 */
public class KillItem implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity instanceof EntityItem;
	}
}
