package mapmakingtools.core.helper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class TileEntityHelper {
	
	/**
	 * See {@link #copyTileEntity(TileEntity)}
	 * @param world The world the #TileEntity is in
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @param z The Z coordinate
	 * @return A new #TileEntity
	 */
	public static TileEntity copyTileEntity(World world, int x, int y, int z) {
		return copyTileEntity(world.getBlockTileEntity(x, y, z));
	}
	
	/**
	 * Copys all the data from the given #TileEntity and creates a new instance with
	 * all the same variables as the first one.
	 * @param tile The #TileEntity that you want to duplicate
	 * @return A new #TileEntity
	 */
	public static TileEntity copyTileEntity(TileEntity tile) {
		if(tile != null) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tile.writeToNBT(tagCompound);
			TileEntity newTile = TileEntity.createAndLoadEntity(tagCompound);
			return newTile;
		}
		return null;
	}
	
	public static void placeTileEntity(TileEntity tile, World world, int x, int y, int z) {
		if(tile != null) {
			tile.validate();
			NBTTagCompound tag = new NBTTagCompound();
			tile.writeToNBT(tag);
			tag.setInteger("x", x);
			tag.setInteger("y", y);
			tag.setInteger("z", z);
			tile.readFromNBT(tag);
			world.setBlockTileEntity(x, y, z, tile);
		}
	}
}
