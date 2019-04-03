package mapmakingtools.api.filter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FilterBase {
	
	/**
	 * @param playerIn The player who clicked on the entity
	 * @param entityIn The entity in question
	 * @return If this filter should
	 */
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return false;
	}
	
	public boolean isApplicable(EntityPlayer playerIn, World worldIn, BlockPos posIn) {
		return false;
	}
	
	public enum TargetType {

		BLOCK(),
		ENTITY();
	}
}
