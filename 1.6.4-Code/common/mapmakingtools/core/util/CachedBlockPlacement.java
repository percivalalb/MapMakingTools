package mapmakingtools.core.util;

import mapmakingtools.api.manager.FlippedManager;
import mapmakingtools.api.manager.RotationManager;
import mapmakingtools.core.helper.TileEntityHelper;
import mapmakingtools.core.helper.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CachedBlockPlacement {

	/** A invalidated tile entity copyed from the one at the x, y, z **/
	private TileEntity  tileEntity;
	/** The block id of the inexistent block at the x, y, z **/
	private int 		blockId;
	/** The block meta of the inexistent block at the x, y, z **/
	private int 		blockMeta;
	/** The X coordinate of where this #CachedBlockPlacement located **/
	private int 		x;
	/** The Y coordinate of where this #CachedBlockPlacement located **/
	private int 		y;
	/** The Z coordinate of where this #CachedBlockPlacement located **/
	private int 		z;
	/** The World of where this #CachedBlockPlacement located **/
	private World 		world;
	
	public CachedBlockPlacement(CachedBlockPlacement cache) {
		this(cache.world, cache.x, cache.y, cache.z);
	}
	
	public CachedBlockPlacement(World world, int x, int y, int z) {
		this.blockId = world.getBlockId(x, y, z);
		this.blockMeta = world.getBlockMetadata(x, y, z);
		this.tileEntity = TileEntityHelper.copyTileEntity(world, x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	/**
	 * Only used for combining mob spawners!
	 */
	public CachedBlockPlacement(World world, TileEntity tile, int x, int y, int z) {
		this.blockId = Block.mobSpawner.blockID;
		this.blockMeta = 0;
		this.tileEntity = tile;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public CachedBlockPlacement(World world, int x, int y, int z, EntityPlayer player) {
		int posX = MathHelper.floor_double(player.posX);
		int posY = MathHelper.floor_double(player.posY);
		int posZ = MathHelper.floor_double(player.posZ);
		this.blockId = world.getBlockId(x, y, z);
		this.blockMeta = world.getBlockMetadata(x, y, z);
		this.tileEntity = TileEntityHelper.copyTileEntity(world, x, y, z);
		this.x = x - posX;
		this.y = y - posY;
		this.z = z - posZ;
		this.world = world;
	}
	
	public void clearTileEntity(World world, int x, int y, int z) {
		try {
			world.removeBlockTileEntity(x, y, z);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public TileEntity getTileEntity() {
		return this.tileEntity;
	}
	
	public int getBlockId() {
		return this.blockId;
	}
	
	public int getBlockMeta() {
		return this.blockMeta;
	}
	
	public int x() {
		return x;
	}
	public int y() {
		return y;
	}
	public int z() {
		return z;
	}
	
	public CachedBlockPlacement setCachedBlock() { 
		CachedBlockPlacement replacementCache = new CachedBlockPlacement(this); //Creates a new Instance with the changes to the block at the classes, x, y, z
		this.clearTileEntity(world, x, y, z);
		WorldHelper.setBlockCheaply(world, x, y, z, blockId, blockMeta); 		//Sets the block data with no block update
		TileEntityHelper.placeTileEntity(tileEntity, world, x, y, z); 			//Places the copied #TileEntity at the classes x, y, z
		if(!world.isRemote && tileEntity != null) { 
			MinecraftServer server = MinecraftServer.getServer();
			Packet packet = tileEntity.getDescriptionPacket();
			if(packet != null) 
				server.getConfigurationManager().sendToAllNear(x + 0.5D, y + 0.5D, z + 0.5D, 256 * 256, world.provider.dimensionId, packet);
		}
		return replacementCache; 												//Returns the replacement cache
	}
	
	public CachedBlockPlacement setCachedBlockReletiveToRotated(EntityPlayer player) { 
		int rotation = DataStorage.getRotation(player);
		int posX = MathHelper.floor_double(player.posX);
		int posY = MathHelper.floor_double(player.posY);
		int posZ = MathHelper.floor_double(player.posZ);
		int newX = x;
		int newY = y;
		int newZ = z;
		int backUpX = x;
		int backUpY = y;
		int backUpZ = z;
		
		switch(rotation) {
		case 0:
			break;
		case 90:
			newX = -backUpZ;
			newZ = backUpX;
			break;
		case 180:
			newX = -backUpX;
			newZ = -backUpZ;
			break;
		case 270:
			newX = backUpZ;
			newZ = -backUpX;
			break;
		}
		CachedBlockPlacement replacementCache = new CachedBlockPlacement(player.worldObj, posX + newX, posY + newY, posZ + newZ); //Creates a new Instance with the changes to the block at the classes, x, y, z
		this.clearTileEntity(world, posX + newX, posY + newY, posZ + newZ);
		WorldHelper.setBlockCheaply(player.worldObj, posX + newX, posY + newY, posZ + newZ, blockId, blockMeta); 		//Sets the block data with no block update
		TileEntityHelper.placeTileEntity(tileEntity, player.worldObj, posX + newX, posY + newY, posZ + newZ); 			//Places the copied #TileEntity at the classes x, y, z
		RotationManager.onBlockRotation(this, blockId, player.worldObj, posX + newX, posY + newY, posZ + newZ, rotation);
		if(!world.isRemote && tileEntity != null) { 
			MinecraftServer server = MinecraftServer.getServer();
			Packet packet = tileEntity.getDescriptionPacket();
			if(packet != null) 
				server.getConfigurationManager().sendToAllNear(x + 0.5D, y + 0.5D, z + 0.5D, 256 * 256, world.provider.dimensionId, packet);
		}
		return replacementCache; 												//Returns the replacement cache
	}
	
	public CachedBlockPlacement setCachedBlockReletiveToFlipped(EntityPlayer player, int flipMode, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) { 
		int newX = x;
		int newY = y;
		int newZ = z;
		int backUpX = x;
		int backUpY = y;
		int backUpZ = z;
		
		switch(flipMode) {
		case 0: //Y Axis
			newY = maxY - (y - minY) - 1;
			break;
		case 1: // X Axis
			newX = maxX - (x - minX) - 1;
			break;
		case 2: //Z Axis
			newZ = maxZ - (z - minZ) - 1;
			break;
		}
		CachedBlockPlacement replacementCache = new CachedBlockPlacement(player.worldObj, newX, newY, newZ); //Creates a new Instance with the changes to the block at the classes, x, y, z
		this.clearTileEntity(world, newX, newY, newZ);
		WorldHelper.setBlockCheaply(player.worldObj, newX, newY, newZ, blockId, blockMeta); 		//Sets the block data with no block update
		TileEntityHelper.placeTileEntity(tileEntity, player.worldObj, newX, newY, newZ); 			//Places the copied #TileEntity at the classes x, y, z
		FlippedManager.onBlockFlipped(this, blockId, player.worldObj, newX, newY, newZ, flipMode);
		if(!world.isRemote && tileEntity != null) { 
			MinecraftServer server = MinecraftServer.getServer();
			Packet packet = tileEntity.getDescriptionPacket();
			if(packet != null) 
				server.getConfigurationManager().sendToAllNear(x + 0.5D, y + 0.5D, z + 0.5D, 256 * 256, world.provider.dimensionId, packet);
		}
		return replacementCache; 												//Returns the replacement cache
	} 
}
