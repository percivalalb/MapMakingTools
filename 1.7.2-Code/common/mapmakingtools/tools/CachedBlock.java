package mapmakingtools.tools;

import cpw.mods.fml.common.FMLLog;
import mapmakingtools.api.enums.Rotation;
import mapmakingtools.api.manager.FlippedManager;
import mapmakingtools.api.manager.RotationManager;
import mapmakingtools.handler.EntityJoinWorldHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * @author ProPercivalalb
 */
public class CachedBlock {

	public World orginalWorld;
	public int x, y, z;
	public Block block;
	public int meta;
	public TileEntity tileEntity;
	
	public CachedBlock(NBTTagCompound tag) { 
		this.readFromNBT(tag); 
	}
	
	public CachedBlock(CachedBlock cache) {
		this(cache.orginalWorld, cache.x, cache.y, cache.z);
	}
	
	public CachedBlock(World world, int x, int y, int z, EntityPlayer player) {
		this(world, x, y, z);
		this.x -= MathHelper.floor_double(player.posX);
		this.y -= MathHelper.floor_double(player.posY);		
		this.z -= MathHelper.floor_double(player.posZ);
	}
	
	public CachedBlock(World world, int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.orginalWorld = world;
		this.block = world.getBlock(x, y, z);
		this.meta = world.getBlockMetadata(x, y, z);
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity != null) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tileEntity.writeToNBT(tagCompound);
			this.tileEntity = TileEntity.createAndLoadEntity(tagCompound);
		}
	}
	
	public void clearTileEntity(World world, int x, int y, int z) {
		world.setBlock(x, y, z, Blocks.air, 0, 2);
		world.removeTileEntity(x, y, z);
	}
	
	public CachedBlock setCachedBlock() { 
		CachedBlock replacementCache = new CachedBlock(this);
		//Stops any entities being destroyed from the block break
		EntityJoinWorldHandler.shouldSpawnEntities = false;
		this.clearTileEntity(this.orginalWorld, this.x, this.y, this.z);
		this.orginalWorld.setBlock(this.x, this.y, this.z, this.block, 0, 2);
		this.orginalWorld.setBlockMetadataWithNotify(this.x, this.y, this.z, this.meta, 2);
		if(this.tileEntity != null)
			this.orginalWorld.setTileEntity(this.x, this.y, this.z, this.tileEntity);
		EntityJoinWorldHandler.shouldSpawnEntities = true;
		
		return replacementCache;
	}
	
	public CachedBlock setCachedBlockReletiveToRotated(PlayerData data, Rotation rotation) { 
		int posX = MathHelper.floor_double(data.getPlayer().posX);
		int posY = MathHelper.floor_double(data.getPlayer().posY);
		int posZ = MathHelper.floor_double(data.getPlayer().posZ);
		int newX = x;
		int newY = y;
		int newZ = z;
		int backUpX = x;
		int backUpY = y;
		int backUpZ = z;
		
		switch(rotation) {
		case _000_:
			break;
		case _090_:
			newX = -backUpZ;
			newZ = backUpX;
			break;
		case _180_:
			newX = -backUpX;
			newZ = -backUpZ;
			break;
		case _270_:
			newX = backUpZ;
			newZ = -backUpX;
			break;
		}
		CachedBlock replacementCache = new CachedBlock(data.getPlayer().worldObj, posX + newX, posY + newY, posZ + newZ);
		//Stops any entities being destroyed from the block break
		EntityJoinWorldHandler.shouldSpawnEntities = false;
		this.clearTileEntity(data.getPlayer().worldObj, posX + newX, posY + newY, posZ + newZ);
		data.getPlayer().worldObj.setBlock(posX + newX, posY + newY, posZ + newZ, this.block, 0, 2);
		data.getPlayer().worldObj.setBlockMetadataWithNotify(posX + newX, posY + newY, posZ + newZ, this.meta, 2);
		if(this.tileEntity != null)
			data.getPlayer().worldObj.setTileEntity(posX + newX, posY + newY, posZ + newZ, this.tileEntity);
		try {
			if(rotation != Rotation._000_)
				RotationManager.onBlockRotation(this.block, this.meta, this.tileEntity, this.orginalWorld, posX + newX, posY + newY, posZ + newZ, rotation);
		}
		catch(Exception e) {}
		EntityJoinWorldHandler.shouldSpawnEntities = true;
		
		return replacementCache; 												
	}
	
	public CachedBlock setCachedBlockReletiveToFlipped(PlayerData data, int flipMode) { 
		int newX = this.x, newY = this.y, newZ = this.z;
		
		if(flipMode == 1)
			newX = data.getMaxX() - (this.x - data.getMinX());
		else if(flipMode == 2)
			newZ = data.getMaxZ() - (this.z - data.getMinZ());
		else if(flipMode == 0)
			newY = data.getMaxY() - (this.y - data.getMinY());
	
		CachedBlock replacementCache = new CachedBlock(this.orginalWorld, newX, newY, newZ);
		//Stops any entities being destroyed from the block break
		EntityJoinWorldHandler.shouldSpawnEntities = false;
		this.clearTileEntity(this.orginalWorld, newX, newY, newZ);
		this.orginalWorld.setBlock(newX, newY, newZ, this.block, 0, 2);
		this.orginalWorld.setBlockMetadataWithNotify(newX, newY, newZ, this.meta, 2);
		if(this.tileEntity != null)
			this.orginalWorld.setTileEntity(newX, newY, newZ, this.tileEntity);
		try {
			FlippedManager.onBlockFlipped(this.block, this.meta, this.tileEntity, this.orginalWorld, newX, newY, newZ, flipMode);
		}
		catch(Exception e) {}
		EntityJoinWorldHandler.shouldSpawnEntities = true;
		
		return replacementCache;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("dimension", this.orginalWorld.provider.dimensionId);
		tag.setInteger("x", this.x);
		tag.setInteger("y", this.y);
		tag.setInteger("z", this.z);
		tag.setString("block", Block.blockRegistry.getNameForObject(this.block));
		tag.setInteger("meta", this.meta);
		if(this.tileEntity != null) {
			NBTTagCompound tileEntityData = new NBTTagCompound();
			this.tileEntity.writeToNBT(tileEntityData);
			tag.setTag("tileEntity", tileEntityData);
		}
		return tag;
	}
	
	public CachedBlock readFromNBT(NBTTagCompound tag) {
		this.orginalWorld = DimensionManager.getWorld(tag.getInteger("dimension"));
		this.x = tag.getInteger("x");
		this.y = tag.getInteger("y");
		this.z = tag.getInteger("z");
		this.block = Block.getBlockFromName(tag.getString("block"));
		this.meta = tag.getInteger("meta");
		if(tag.hasKey("tileEntity"))
			this.tileEntity = TileEntity.createAndLoadEntity(tag.getCompoundTag("tileEntity"));
		return this;
	}
	
}
