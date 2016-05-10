package mapmakingtools.tools;


/**
 * @author ProPercivalalb
 */
public class CachedBlock {
	/**
	public World orginalWorld;
	public BlockPos pos;
	public IBlockState blockState;
	public NBTTagCompound tileEntity;
	
	public CachedBlock(NBTTagCompound tag, boolean useWorld) { 
		this.readFromNBT(tag, useWorld); 
	}
	
	public CachedBlock(DataInputStream tag, boolean useWorld) throws IOException { 
		this.readFromInputStream(tag, useWorld);
	}
	
	public CachedBlock(CachedBlock cache) {
		this(cache.orginalWorld, cache.pos);
	}
	
	public CachedBlock(World world, BlockPos pos, EntityPlayer player) {
		this(world, pos.add(-MathHelper.floor_double(player.posX), -MathHelper.floor_double(player.posY), -MathHelper.floor_double(player.posZ)));
	}
	
	public CachedBlock(World world, BlockPos pos) {
		this.pos = pos;
		this.orginalWorld = world;
		this.blockState = world.getBlockState(this.pos);
		TileEntity tileEntity = world.getTileEntity(this.pos);
		if(tileEntity != null)
			tileEntity.writeToNBT(this.tileEntity = new NBTTagCompound());
	}
	
	public void setWorld(World world) {
		this.orginalWorld = world;
	}
	
	public void clearTileEntity(World world, BlockPos pos) {
		world.setBlockState(pos, Blocks.air.getDefaultState(), 2);
		world.removeTileEntity(pos);
	}
	
	public CachedBlock setCachedBlock() { 
		CachedBlock replacementCache = new CachedBlock(this);
		//Stops any entities being destroyed from the block break
		EntityJoinWorldHandler.shouldSpawnEntities = false;
		this.clearTileEntity(this.orginalWorld, this.pos);
		this.orginalWorld.setBlockState(this.pos, this.blockState, 2);
		
		if(this.tileEntity != null)
			this.orginalWorld.setTileEntity(this.pos, TileEntity.createAndLoadEntity(this.tileEntity));
		
		EntityJoinWorldHandler.shouldSpawnEntities = true;
		
		return replacementCache;
	}
	
	public CachedBlock setCachedBlockReletiveToRotated(PlayerData data, MovementType movementType) { 
		BlockPos playerPos = new BlockPos(data.getPlayer());
		BlockPos newPos = new BlockPos(this.pos);
		
		switch(movementType) {
		case _000_:
			break;
		case _090_:
			newPos = new BlockPos(-this.pos.getZ(), this.pos.getY(), this.pos.getX());
			break;
		case _180_:
			newPos = new BlockPos(-this.pos.getX(), this.pos.getY(), -this.pos.getZ());
			break;
		case _270_:
			newPos = new BlockPos(this.pos.getZ(), this.pos.getY(), -this.pos.getX());
			break;
		}
		
		newPos = newPos.add(playerPos);
		
		CachedBlock replacementCache = new CachedBlock(data.getPlayer().worldObj, newPos);
		//Stops any entities being destroyed from the block break
		EntityJoinWorldHandler.shouldSpawnEntities = false;
		try {
			this.clearTileEntity(data.getPlayer().worldObj, newPos);
			boolean set = RotationLoader.onRotation(this.blockState, this.tileEntity, this.orginalWorld, newPos, movementType);
			
			if(!set)
				data.getPlayer().worldObj.setBlockState(newPos, this.blockState, 2);
			
			if(this.tileEntity != null) {
				TileEntity tile = TileEntity.createAndLoadEntity(this.tileEntity);
				tile.setPos(newPos);
				data.getPlayer().worldObj.setTileEntity(newPos, tile);
			}
		}
		catch(Exception e) {}
		EntityJoinWorldHandler.shouldSpawnEntities = true;
		
		return replacementCache; 												
	}
	
	public CachedBlock setCachedBlockReletiveToFlipped(PlayerData data, MovementType movementType) { 
		int newX = this.x, newY = this.y, newZ = this.z;
		
		switch(movementType) {
		case _X_:
			newX = data.getMaxX() - (this.x - data.getMinX());
			break;
		case _Z_:
			newZ = data.getMaxZ() - (this.z - data.getMinZ());
			break;
		case _Y_:
			newY = data.getMaxY() - (this.y - data.getMinY());
			break;
		}
	
		CachedBlock replacementCache = new CachedBlock(this.orginalWorld, newX, newY, newZ);
		//Stops any entities being destroyed from the block break
		EntityJoinWorldHandler.shouldSpawnEntities = false;
		try {
			this.clearTileEntity(this.orginalWorld, newX, newY, newZ);
			boolean set = RotationLoader.onRotation(blockState, tileEntity, this.orginalWorld,  newX, newY, newZ, movementType);
			
			if(!set)
				this.orginalWorld.setBlock(newX, newY, newZ, this.blockState, this.meta, 2);
		
			if(this.tileEntity != null) {
				this.tileEntity.xCoord = newX;
				this.tileEntity.yCoord = newY;
				this.tileEntity.zCoord = newZ;
				this.orginalWorld.setTileEntity(newX, newY, newZ, this.tileEntity);
			}
		}
		catch(Exception e) {}
		EntityJoinWorldHandler.shouldSpawnEntities = true;
		
		return replacementCache;
	}
	
	public void writeToOutputStream(DataOutputStream data, boolean useWorld) throws IOException {
		if(useWorld)
			packetbuffer.writeInt(this.orginalWorld.provider.getDimensionId());
		
		data.writeLong(this.pos.toLong());
		packetbuffer.writeString(Block.blockRegistry.getNameForObject(this.blockState));
		PacketHelper.writeNBTTagCompound(this.tileEntity, data);
	}
	
	public CachedBlock readFromInputStream(DataInputStream data, boolean useWorld) throws IOException {
		if(useWorld)
			this.orginalWorld = DimensionManager.getWorld(packetbuffer.readInt());
		
		this.pos = BlockPos.fromLong(data.readLong());
		this.blockState = Block.getBlockFromName(packetbuffer.readStringFromBuffer(Integer.MAX_VALUE));
		this.tileEntity = PacketHelper.readNBTTagCompound(data);
		return this;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag, boolean useWorld) {
		if(useWorld)
			tag.setInteger("dimension", this.orginalWorld.provider.getDimensionId());
		
		tag.setLong("pos", this.pos.toLong());
		tag.setString("block", Block.blockRegistry.getNameForObject(this.blockState.getBlock()));
		if(this.tileEntity != null)
			tag.setTag("tileEntity", this.tileEntity);
		return tag;
	}
	
	public CachedBlock readFromNBT(NBTTagCompound tag, boolean useWorld) {
		if(useWorld)
			this.orginalWorld = DimensionManager.getWorld(tag.getInteger("dimension"));
		
		this.pos = BlockPos.fromLong(tag.getLong("pos"));
		this.blockState = Block.getBlockFromName(tag.getString("block"));
		if(tag.hasKey("tileEntity"))
			this.tileEntity = tag.getCompoundTag("tileEntity");
		return this;
	}**/
	
}
