package mapmakingtools.api.manager;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.SideHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class FakeWorldManager {

	private static TileEntity FAKE_TILE_ENTITY;
	private static NBTTagCompound FAKE_TILE_ENTITY_DATA;

	private static Entity FAKE_ENTITY;
	private static NBTTagCompound FAKE_ENTITY_DATA;
	
	public static void putTileEntity(TileEntity tileEntity, World world, BlockPos pos, NBTTagCompound dataRecived) {
		try {
			TileEntity newTileEntity = TileEntity.create(world, dataRecived);
			FAKE_TILE_ENTITY = newTileEntity;
			FAKE_TILE_ENTITY_DATA = dataRecived;
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void putEntity(Entity entity, NBTTagCompound dataRecived) {
		try {
			Entity newEntity = EntityList.createEntityFromNBT(dataRecived, entity.world);
			FAKE_ENTITY = newEntity;
			FAKE_ENTITY_DATA = dataRecived;
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static Entity getEntity(World world, int entityId) {
		if(SideHelper.isServer())
			return world.getEntityByID(entityId);
		
		// Sanity check
		if(FAKE_ENTITY == null)
			MapMakingTools.LOGGER.error("Entity fake failed sanity check");
		
		return FAKE_ENTITY;
	}
	
	public static TileEntity getTileEntity(World world, BlockPos pos) {
		if(SideHelper.isServer())
			return world.getTileEntity(pos);
		
		// Sanity check
		if(FAKE_TILE_ENTITY == null || !FAKE_TILE_ENTITY.getPos().equals(pos))
			MapMakingTools.LOGGER.error("TileEntity fake failed sanity check");
		
		return FAKE_TILE_ENTITY;
	}
}
