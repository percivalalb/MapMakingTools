package mapmakingtools.api.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public abstract class IFilter {
	
	public boolean isApplicable(EntityPlayer player, Entity entity) { return false; }
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) { return false; }
}
