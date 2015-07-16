package mapmakingtools.api.manager;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class FakeWorldManager {

	private static Hashtable<List<Comparable>, TileEntity> fakeTileEntities = new Hashtable<List<Comparable>, TileEntity>();

	public static void putTileEntity(TileEntity tileEntity, World world, BlockPos pos, NBTTagCompound dataRecived) {
		try {
			List<Comparable> key = Arrays.asList(world.provider.getDimensionId(), pos);
			
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
			entity.readFromNBT(dataRecived);
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static TileEntity getTileEntity(World world, BlockPos pos) {
		List<Comparable> key = Arrays.asList(world.provider.getDimensionId(), pos);
		return fakeTileEntities.get(key);
	}
}
