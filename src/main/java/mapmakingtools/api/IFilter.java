package mapmakingtools.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public abstract class IFilter {
	
	public boolean isApplicable(EntityPlayer player, Entity entity) { return false; }
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) { return false; }
}
