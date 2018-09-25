package mapmakingtools.api.filter;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IFilterProvider {

	public void addFilterClientToBlockList(EntityPlayer player, World world, BlockPos pos, List<FilterClient> filterList);
	public void addFilterServerToBlockList(EntityPlayer player, World world, BlockPos pos, List<FilterServer> filterList);
	public void addFilterClientToEntityList(EntityPlayer player, Entity entity, List<FilterClient> filterList);
	public void addFilterServerToEntityList(EntityPlayer player, Entity entity, List<FilterServer> filterList);
}
