package mapmakingtools.api.interfaces;

import net.minecraft.entity.Entity;

/**
 * @author ProPercivalalb
 */
public interface IForceKill {

	/**
	 * If returned true 1 will be added to the total amount of entities killed and the entity
	 * is deleted from the work
	 * @param entity The target entity
	 * @return If the entity is can be killed
	 */
	public boolean onCommand(Entity entity);
}
