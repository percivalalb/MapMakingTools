package mapmakingtools.tools.killentities;

import mapmakingtools.api.interfaces.IForceKill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

/**
 * @author ProPercivalalb
 */
public class KillItem implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity instanceof EntityItem;
	}
}
