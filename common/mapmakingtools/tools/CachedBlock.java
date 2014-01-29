package mapmakingtools.tools;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
	
	public CachedBlock(NBTTagCompound tag) { this.readFromNBT(tag); }
	
	public CachedBlock(CachedBlock cache) {
		this(cache.orginalWorld, cache.x, cache.y, cache.z);
	}
	
	public CachedBlock(World world, int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.orginalWorld = world;
		this.block = world.func_147439_a(x, y, z);
		this.meta = world.getBlockMetadata(x, y, z);
		TileEntity tileEntity = world.func_147438_o(x, y, z);
		if(tileEntity != null) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tileEntity.func_145841_b(tagCompound);
			this.tileEntity = TileEntity.func_145827_c(tagCompound);
		}
	}
	
	public CachedBlock setCachedBlock() { 
		CachedBlock replacementCache = new CachedBlock(this);
		this.clearTileEntity(this.orginalWorld, this.x, this.y, this.z);
		this.orginalWorld.func_147465_d(this.x, this.y, this.z, this.block, this.meta, 2);
		if(this.tileEntity != null)
			this.orginalWorld.func_147455_a(this.x, this.y, this.z, this.tileEntity);
		return replacementCache;
	}
	
	public void clearTileEntity(World world, int x, int y, int z) {
		world.func_147475_p(x, y, z);
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("dimension", this.orginalWorld.provider.dimensionId);
		tag.setInteger("x", this.x);
		tag.setInteger("y", this.y);
		tag.setInteger("z", this.z);
		tag.setString("block", Block.field_149771_c.func_148750_c(this.block));
		tag.setInteger("meta", this.meta);
		if(this.tileEntity != null) {
			NBTTagCompound tileEntityData = new NBTTagCompound();
			this.tileEntity.func_145841_b(tileEntityData);
			tag.setTag("tileEntity", tileEntityData);
		}
		return tag;
	}
	
	public CachedBlock readFromNBT(NBTTagCompound tag) {
		this.orginalWorld = DimensionManager.getWorld(tag.getInteger("dimension"));
		this.x = tag.getInteger("x");
		this.y = tag.getInteger("y");
		this.z = tag.getInteger("z");
		this.block = Block.func_149684_b(tag.getString("block"));
		this.meta = tag.getInteger("meta");
		if(tag.hasKey("tileEntity"))
			this.tileEntity = TileEntity.func_145827_c(tag.getCompoundTag("tileEntity"));
		return this;
	}
	
}
