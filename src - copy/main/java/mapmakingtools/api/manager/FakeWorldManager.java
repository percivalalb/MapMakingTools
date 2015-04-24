package mapmakingtools.api.manager;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class FakeWorldManager {

	private static Hashtable<List<Integer>, TileEntity> fakeTileEntities = new Hashtable<List<Integer>, TileEntity>();
	private static Hashtable<Integer, Entity> fakeEntities = new Hashtable<Integer, Entity>();

	public static void putTileEntity(TileEntity tileEntity, World world, int x, int y, int z, NBTTagCompound dataRecived) {
		try {
			List<Integer> key = Arrays.asList(world.provider.dimensionId, x, y, z);
			
			TileEntity newTileEntity = tileEntity.getClass().newInstance();
			newTileEntity.readFromNBT(dataRecived);
			fakeTileEntities.put(key, newTileEntity);
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void putEntity(Entity entity, NBTTagCompound dataRecived) {
		try {
			Entity newEntity = EntityList.createEntityByName(EntityList.getEntityString(entity), null);
			newEntity.readFromNBT(dataRecived);
			fakeEntities.put(entity.getEntityId(), newEntity);
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static TileEntity getTileEntity(World world, int x, int y, int z) {
		List<Integer> key = Arrays.asList(world.provider.dimensionId, x, y, z);
		return fakeTileEntities.get(key);
	}
	
	public static Entity getEntity(int entityId) {
		return fakeEntities.get(entityId);
	}
}
