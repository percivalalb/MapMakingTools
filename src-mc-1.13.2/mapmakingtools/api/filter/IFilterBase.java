package mapmakingtools.api.filter;

import mapmakingtools.api.filter.FilterBase.TargetType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFilterBase {
	
	public TargetType getTargetType();
	public BlockPos getBlockPos();
	public int getEntityId();
	public Entity getEntity();
	
	public World getWorld();
	public EntityPlayer getPlayer();
}
