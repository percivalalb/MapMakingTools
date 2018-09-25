package mapmakingtools.api.interfaces;

import mapmakingtools.api.enums.TargetType;
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
