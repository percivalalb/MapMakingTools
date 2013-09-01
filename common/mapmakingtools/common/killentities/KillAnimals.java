package mapmakingtools.common.killentities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import mapmakingtools.api.IForceKill;

/**
 * @author ProPercivalalb
 */
public class KillAnimals implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity instanceof EntityAnimal;
	}
}
