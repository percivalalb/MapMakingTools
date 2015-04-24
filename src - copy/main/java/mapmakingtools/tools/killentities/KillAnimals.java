package mapmakingtools.tools.killentities;

import mapmakingtools.api.interfaces.IForceKill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;

/**
 * @author ProPercivalalb
 */
public class KillAnimals implements IForceKill {

	@Override
	public boolean onCommand(Entity entity) {
		return entity instanceof EntityAnimal;
	}
}
