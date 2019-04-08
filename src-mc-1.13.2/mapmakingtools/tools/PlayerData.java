package mapmakingtools.tools;

import java.util.Hashtable;
import java.util.UUID;

import javax.annotation.Nullable;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.helper.SideHelper;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.network.packet.PacketSetPoint;
import mapmakingtools.network.packet.PacketSetPoint2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class PlayerData {

	private Hashtable<Integer, BlockPos> DIMID_POS1 = new Hashtable<Integer, BlockPos>();
	private Hashtable<Integer, BlockPos> DIMID_POS2 = new Hashtable<Integer, BlockPos>();
	private ActionStorage actionStorage = new ActionStorage(this);
	
	private UUID uuid;
	
	public PlayerData(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
	
	public PlayerData(UUID uuid) {
		this.uuid = uuid;
	}
	
	public EntityPlayer getPlayer() {
		if(SideHelper.isClient()) {
			//MapMakingTools.LOGGER.info("CLIENT");
			//MapMakingTools.LOGGER.info("NULL" + MapMakingTools.PROXY.getPlayerEntity() == null);
			return MapMakingTools.PROXY.getPlayerEntity();
		}
		//MapMakingTools.LOGGER.info("SERVER");
		return ServerHelper.getServer().getPlayerList().getPlayerByUUID(this.uuid);
	}
	
	public World getPlayerWorld() {
		return this.getPlayer().world;
	}
	
	@Nullable
	public BlockPos getFirstPoint(int dimId) {	
		return this.DIMID_POS1.get(dimId);
	}
	
	@Nullable
	public BlockPos getFirstPoint() {	
		return this.getFirstPoint(this.getPlayer()
				.world
				.dimension
				.getType()
				.getId());
	}
	
	@Nullable
	public BlockPos getSecondPoint(int dimId) {
		return this.DIMID_POS2.get(dimId);
	}
	
	@Nullable
	public BlockPos getSecondPoint() {
		return this.getSecondPoint(this.getPlayer().world.dimension.getType().getId());
	}
	
	
	public int[] getSelectionSize() {
		return new int[] {this.getMaxX() - this.getMinX() + 1, this.getMaxY() - this.getMinY() + 1, this.getMaxZ() - this.getMinZ() + 1};
	}
	
	public int getBlockCount() {
		return (this.getMaxX() - this.getMinX() + 1) * (this.getMaxZ() - this.getMinZ() + 1) * (this.getMaxY() - this.getMinY() + 1);
	}
	
	public boolean hasSelectedPoints() {
		return this.getFirstPoint() != null && this.getSecondPoint() != null;
	}
	
	public boolean setFirstPoint(BlockPos pos) {
		int dimId = this.getPlayer().world.dimension.getType().getId();
		if(pos != null)
			this.DIMID_POS1.put(dimId, pos.toImmutable());
		else
			this.DIMID_POS1.remove(dimId);
		return true;
	}
	
	public boolean setPoints(BlockPos pos1, BlockPos pos2) {
		this.setFirstPoint(pos1);
		this.setSecondPoint(pos2);
		return true;
	}
	
	public boolean setSecondPoint(BlockPos pos) {
		int dimId = this.getPlayer().world.dimension.getType().getId();
		if(pos != null)
			this.DIMID_POS2.put(dimId, pos.toImmutable());
		else
			this.DIMID_POS2.remove(dimId);
		return true;
	}
	
	public void sendUpdateToClient(EntityPlayer player) {
		if(player instanceof EntityPlayerMP) {
			PacketHandler.send(PacketDistributor.PLAYER.with(() -> (EntityPlayerMP)player), new PacketSetPoint(this.getFirstPoint(player.world.dimension.getType().getId())));
			PacketHandler.send(PacketDistributor.PLAYER.with(() -> (EntityPlayerMP)player), new PacketSetPoint2(this.getSecondPoint(player.world.dimension.getType().getId())));
		}
		else
			MapMakingTools.LOGGER.warn("Tried to update client but player was not instanceof EntityPlayerMP");
	}
	
	public ActionStorage getActionStorage() {
		return this.actionStorage;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list1 = new NBTTagList();
		NBTTagList list2 = new NBTTagList();
		
		for(int key : this.DIMID_POS1.keySet()) {
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.putInt("dim", key);
			tag1.putLong("pos", DIMID_POS1.get(key).toLong());
			list1.add(tag1);
		}
		
		for(int key : this.DIMID_POS2.keySet()) {
			NBTTagCompound tag2 = new NBTTagCompound();
			tag2.putInt("dim", key);
			tag2.putLong("pos", DIMID_POS2.get(key).toLong());
			list2.add(tag2);
		}
		
		tag.putUniqueId("uuid", this.uuid);
		tag.put("firstPoint", list1);
		tag.put("secondPoint", list2);
		
		return tag;
	}
	
	public PlayerData readFromNBT(NBTTagCompound tag) {
		NBTTagList list1 = (NBTTagList)tag.get("firstPoint");
		NBTTagList list2 = (NBTTagList)tag.get("secondPoint");
		
		for(int i = 0; i < list1.size(); ++i) {
			NBTTagCompound tag1 = list1.getCompound(i);
			int dim = tag1.getInt("dim");
			DIMID_POS1.put(dim, BlockPos.fromLong(tag1.getLong("pos")));
		}
		
		for(int i = 0; i < list2.size(); ++i) {
			NBTTagCompound tag2 = list2.getCompound(i);
			int dim = tag2.getInt("dim");
			DIMID_POS2.put(dim, BlockPos.fromLong(tag2.getLong("pos")));
		}
		
		this.uuid = tag.getUniqueId("uuid");
		return this;
	}
	
	public BlockPos getMinPos() {
		return new BlockPos(this.getMinX(), this.getMinY(), this.getMinZ());
	}
	
	public BlockPos getMaxPos() {
		return new BlockPos(this.getMaxX(), this.getMaxY(), this.getMaxZ());
	}
	
	public int getMinX() {
		return Math.min(this.getFirstPoint().getX(), this.getSecondPoint().getX());
	}
	public int getMinY() {
		return Math.min(this.getFirstPoint().getY(), this.getSecondPoint().getY());
	}
	public int getMinZ() {
		return Math.min(this.getFirstPoint().getZ(), this.getSecondPoint().getZ());
	}
	public int getMaxX() {
		return Math.max(this.getFirstPoint().getX(), this.getSecondPoint().getX());
	}
	public int getMaxY() {
		return Math.max(this.getFirstPoint().getY(), this.getSecondPoint().getY());
	}
	public int getMaxZ() {
		return Math.max(this.getFirstPoint().getZ(), this.getSecondPoint().getZ());
	}
	
	@Override
	public String toString() {
		return "PlayerData=[UUID=" + this.uuid +"]";
	}
}
