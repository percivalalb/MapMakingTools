package mapmakingtools.tools;

import java.io.IOException;

import io.netty.buffer.Unpooled;
import jline.internal.Nullable;
import mapmakingtools.api.enums.MovementType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author ProPercivalalb
 * This class is based off {@link net.minecraftforge.common.util.BlockSnapshot}
 */
public class BlockCache {

	public BlockPos playerPos;
	public BlockPos pos;
	private int dimId;
	
	private transient IBlockState replacedBlock;
	private transient World world;
	
	@Nullable
	private NBTTagCompound nbt;
	private ResourceLocation registryName;
	public int meta;

    private BlockCache() {}
    
    private BlockCache(BlockPos playerPos, World world, BlockPos pos, IBlockState state) {
        this.playerPos = playerPos;
    	this.world = world;
        this.dimId = world.provider.getDimension();
        this.pos = pos.toImmutable();
        this.replacedBlock = state;
        this.registryName = state.getBlock().getRegistryName();
        this.meta = state.getBlock().getMetaFromState(state);
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity != null) {
            this.nbt = new NBTTagCompound();
            tileEntity.writeToNBT(this.nbt);
        }
        else 
        	this.nbt = null;
    }

    private BlockCache(BlockPos playerPos, int dimension, BlockPos pos, String modID, String blockName, int meta, NBTTagCompound nbt) {
    	this(playerPos, dimension, pos, new ResourceLocation(modID, blockName), meta, nbt);
    }
    
    private BlockCache(BlockPos playerPos, int dimension, BlockPos pos, ResourceLocation resource, int meta, NBTTagCompound nbt) {
    	this.playerPos = playerPos;
    	this.dimId = dimension;
        this.pos = pos.toImmutable();
        this.registryName = resource;
        this.meta = meta;
        this.nbt = nbt;
    }

	public static BlockCache createCache(EntityPlayer player, World world, BlockPos pos) {
        return new BlockCache(new BlockPos(player), world, pos, world.getBlockState(pos));
    }
    
    public static BlockCache createCache(BlockPos playerPos, World world, BlockPos pos) {
        return new BlockCache(playerPos, world, pos, world.getBlockState(pos));
    }

    public static BlockCache readFromNBT(NBTTagCompound tag) {
        NBTTagCompound nbt = tag.getBoolean("hasTE") ? null : tag.getCompoundTag("tileEntity");

        return new BlockCache(
        		BlockPos.fromLong(tag.getLong("playerPos")),
        		tag.getInteger("dimension"),
                BlockPos.fromLong(tag.getLong("blockPos")),
                tag.getString("blockMod"),
                tag.getString("blockName"),
                tag.getByte("metadata"),
                nbt);
    }
    
    public static BlockCache readFromPacketBuffer(PacketBuffer packetbuffer) throws IOException {
        return new BlockCache(
        		packetbuffer.readBlockPos(),
        		packetbuffer.readInt(),
        		packetbuffer.readBlockPos(),
        		new ResourceLocation(packetbuffer.readString(Integer.MAX_VALUE / 4)),
                packetbuffer.readByte(),
                packetbuffer.readCompoundTag());
    }
    
    public static BlockCache readFromPacketBufferCompact(PacketBuffer packetbuffer) throws IOException {
        BlockCache cache = new BlockCache();
        cache.registryName = new ResourceLocation(packetbuffer.readString(Integer.MAX_VALUE / 4));
        cache.meta = packetbuffer.readByte();
        cache.nbt = packetbuffer.readCompoundTag();
        return cache;
    }

    public boolean restore(boolean applyPhysics) {
        return this.restoreToLocation(this.getWorld(), this.pos, applyPhysics);
    }

    public boolean restoreToLocation(World world, BlockPos pos, boolean applyPhysics) {

        world.setBlockState(pos, this.getReplacedBlock(), applyPhysics ? 3 : 2);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), this.getReplacedBlock(), applyPhysics ? 3 : 2);
        if(this.nbt != null) {
	    	TileEntity tileEntity = world.getTileEntity(pos);
	        if(tileEntity != null) {
	            tileEntity.readFromNBT(this.nbt);
	            tileEntity.setPos(pos);
	            tileEntity.markDirty();
	        }
	    }
        
        return true;
    }
    
    public BlockCache restoreRelativeToRotated(PlayerData dataIn, Rotation rotationIn) { 
    	World world = dataIn.getPlayerWorld();
    	
		BlockPos diffPos = this.pos.subtract(this.playerPos).rotate(rotationIn);
		BlockPos newPlayerPos = new BlockPos(dataIn.getPlayer());

		diffPos = diffPos.add(newPlayerPos);
		
		BlockCache bse = BlockCache.createCache(dataIn.getPlayer(), dataIn.getPlayerWorld(), diffPos);
		
		IBlockState newState = this.getReplacedBlock().getBlock().withRotation(this.getReplacedBlock(), rotationIn);
		
		dataIn.getPlayerWorld().setBlockState(diffPos, newState, 2);
		
		dataIn.getPlayerWorld().notifyBlockUpdate(diffPos, dataIn.getPlayerWorld().getBlockState(diffPos), newState, 2);
	    if(this.nbt != null) {
	    	TileEntity tileEntity = dataIn.getPlayerWorld().getTileEntity(diffPos);
	        if(tileEntity != null) {
	            tileEntity.readFromNBT(this.nbt);
	            tileEntity.setPos(diffPos);
	            tileEntity.markDirty();
	        }
	    }
	    
	    return bse;
	}
    
    public BlockCache restoreRelative(PlayerData dataIn) { 
		return restoreRelative(dataIn.getPlayerWorld(), new BlockPos(dataIn.getPlayer()));
	}
    
    public BlockCache restoreRelative(World world, BlockPos pos) { 
		BlockPos newPos = this.pos.subtract(this.playerPos);
		BlockPos newPlayerPos = pos;
		newPos = newPos.add(newPlayerPos);

		BlockCache bse = BlockCache.createCache(pos, world, newPos);
		
		this.restoreToLocation(world, newPos, false);
		
		return bse;
	}
	
	public BlockCache restoreRelativeToFlipped(PlayerData data, Mirror mirror) { 
		BlockPos newPos = this.pos;
		
		if(mirror.equals(Mirror.FRONT_BACK))
			newPos = new BlockPos(data.getMaxX() - (this.pos.getX() - data.getMinX()), this.pos.getY(), this.pos.getZ());
		else if(mirror.equals(Mirror.LEFT_RIGHT))
			newPos = new BlockPos(this.pos.getX(), this.pos.getY(), data.getMaxZ() - (this.pos.getZ() - data.getMinZ()));
		//else if(movementType.equals(MovementType._Y_))
		//	newPos = new BlockPos(this.pos.getX(), data.getMaxY() - (this.pos.getY() - data.getMinY()), this.pos.getZ());
	
		BlockCache bse = BlockCache.createCache(data.getPlayer(), data.getPlayerWorld(), newPos);
		
		IBlockState newState = this.getReplacedBlock().getBlock().withMirror(this.getReplacedBlock(), mirror);
		
		//TODO if(!RotationLoader.onRotation(data.getPlayerWorld(), newPos, this.blockIdentifier, this.block, this.meta, movementType))
			data.getPlayerWorld().setBlockState(newPos, newState, 2);
		
		data.getPlayerWorld().notifyBlockUpdate(newPos, data.getPlayerWorld().getBlockState(newPos), newState, 2);
	    if (this.nbt != null) {
	    	TileEntity tileEntity = data.getPlayerWorld().getTileEntity(newPos);
	        if (tileEntity != null) {
	            tileEntity.readFromNBT(this.nbt);
	            tileEntity.setPos(newPos);
	        }
	    }
	    
	    return bse;
	}

    public void writeToNBT(NBTTagCompound compound) {
    	compound.setLong("playerPos", this.playerPos.toLong());
        compound.setString("blockMod", this.registryName.getResourceDomain());
        compound.setString("blockName", this.registryName.getResourcePath());
        compound.setLong("blockPos", this.pos.toLong());
        compound.setInteger("dimension", this.dimId);
        compound.setByte("metadata", (byte)this.meta);

        compound.setBoolean("hasTE", this.nbt != null);

        if(this.nbt != null)
            compound.setTag("tileEntity", this.nbt);
    }
    
    public void writeToPacketBuffer(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.playerPos);
		packetbuffer.writeInt(this.dimId);
		packetbuffer.writeBlockPos(this.pos);
		
	    if(this.registryName.getResourceDomain().equals("minecraft"))
			packetbuffer.writeString(this.registryName.getResourcePath());
	    else
	    	packetbuffer.writeString(this.registryName.toString());
	    
		packetbuffer.writeByte(this.meta);
		packetbuffer.writeCompoundTag(this.nbt);
	}
    
    private static PacketBuffer SIZE_BUFFER = new PacketBuffer(Unpooled.buffer());
    
    public int calculateSizeEverything() throws IOException {
    	SIZE_BUFFER.clear();
    	this.writeToPacketBuffer(SIZE_BUFFER);
    	return SIZE_BUFFER.writerIndex();
    }
    
    public int calculateSizeCompact() throws IOException {
    	SIZE_BUFFER.clear();
    	this.writeToPacketBufferCompact(SIZE_BUFFER);
    	return SIZE_BUFFER.writerIndex();
    }
    
    public void writeToPacketBufferCompact(PacketBuffer packetbuffer) throws IOException {
	    if(this.registryName.getResourceDomain().equals("minecraft"))
			packetbuffer.writeString(this.registryName.getResourcePath());
	    else
	    	packetbuffer.writeString(this.registryName.toString());
	    
		packetbuffer.writeByte(this.meta);
		packetbuffer.writeCompoundTag(this.nbt);
	}

    public World getWorld() {
        return this.world == null ? DimensionManager.getWorld(this.dimId) : this.world;
    }

    public IBlockState getReplacedBlock() {
        return this.replacedBlock == null ? ForgeRegistries.BLOCKS.getValue(this.registryName).getStateFromMeta(this.meta) : this.replacedBlock;
    }

    public TileEntity getTileEntity() {
        return this.nbt != null ? TileEntity.create(this.getWorld(), this.nbt) : null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(this.getClass() != obj.getClass())
            return false;
        BlockCache other = (BlockCache)obj;
        if(!this.pos.equals(other.pos))
            return false;
        if(this.meta != other.meta)
            return false;
        if(this.dimId != other.dimId)
            return false;
        if(this.nbt != other.nbt && (this.nbt == null || !this.nbt.equals(other.nbt)))
            return false;
        if(this.getWorld() != other.getWorld() && (this.getWorld() == null || !this.getWorld().equals(other.getWorld())))
            return false;
        if(this.registryName != other.registryName && (this.registryName == null || !this.registryName.equals(other.registryName)))
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.pos.getX();
        hash = 73 * hash + this.pos.getY();
        hash = 73 * hash + this.pos.getZ();
        hash = 73 * hash + this.meta;
        hash = 73 * hash + this.dimId;
        hash = 73 * hash + (this.nbt != null ? this.nbt.hashCode() : 0);
        hash = 73 * hash + (this.getWorld() != null ? this.getWorld().hashCode() : 0);
        hash = 73 * hash + (this.registryName != null ? this.registryName.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
    	return "BlockCache=[Pos=" + this.pos.toString() + ", Block=" + this.registryName + "]";
    }
}