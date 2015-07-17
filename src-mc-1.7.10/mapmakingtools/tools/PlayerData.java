package mapmakingtools.tools;

import java.util.Hashtable;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLCommonHandler;
import mapmakingtools.MapMakingTools;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketSetPoint1;
import mapmakingtools.network.packet.PacketSetPoint2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class PlayerData {

	private Hashtable<Integer, BlockPos> pos1 = new Hashtable<Integer, BlockPos>();
	private Hashtable<Integer, BlockPos> pos2 = new Hashtable<Integer, BlockPos>();
	private ActionStorage actionStorage = new ActionStorage(this);
	public BlockPos lastPos;
	
	public String username;
	
	public PlayerData() {}
	public PlayerData(String username) {
		this.username = username;
	}
	
	public EntityPlayer getPlayer() {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return MapMakingTools.proxy.getPlayerEntity();
		
		if(Strings.isNullOrEmpty(this.username))
			return null;
		
		return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(this.username);
	}
	
	public World getPlayerWorld() {
		return this.getPlayer().worldObj;
	}
	
	public BlockPos getFirstPoint() {
		int dimId = this.getPlayer().worldObj.provider.dimensionId;	
		return pos1.get(dimId);
	}
	
	public BlockPos getSecondPoint() {
		int dimId = this.getPlayer().worldObj.provider.dimensionId;
		return pos2.get(dimId);
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
		int dimId = this.getPlayer().worldObj.provider.dimensionId;
		if(pos != null)
			pos1.put(dimId, pos);
		else
			pos1.remove(dimId);
		return true;
	}
	
	public boolean setPoints(BlockPos pos1, BlockPos pos2) {
		this.setFirstPoint(pos1);
		this.setSecondPoint(pos2);
		return true;
	}
	
	public boolean setSecondPoint(BlockPos pos) {
		int dimId = this.getPlayer().worldObj.provider.dimensionId;
		if(pos != null)
			pos2.put(dimId, pos);
		else
			pos2.remove(dimId);
		return true;
	}
	
	public void sendUpdateToClient() {
		PacketDispatcher.sendTo(new PacketSetPoint1(this.getFirstPoint()), this.getPlayer());
		PacketDispatcher.sendTo(new PacketSetPoint2(this.getSecondPoint()), this.getPlayer());
	}
	
	public ActionStorage getActionStorage() {
		return this.actionStorage;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list1 = new NBTTagList();
		NBTTagList list2 = new NBTTagList();
		
		for(int key : this.pos1.keySet()) {
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setInteger("dim", key);
			tag1.setLong("pos", pos1.get(key).toLong());
			list1.appendTag(tag1);
		}
		
		for(int key : this.pos2.keySet()) {
			NBTTagCompound tag2 = new NBTTagCompound();
			tag2.setInteger("dim", key);
			tag2.setLong("pos", pos2.get(key).toLong());
			list2.appendTag(tag2);
		}
		
		tag.setString("username", this.username);
		tag.setTag("firstPoint", list1);
		tag.setTag("secondPoint", list2);
		tag.setTag("actionStorage", actionStorage.writeToNBT(new NBTTagCompound()));
		
		return tag;
	}
	
	public PlayerData readFromNBT(NBTTagCompound tag) {
		NBTTagList list1 = (NBTTagList)tag.getTag("firstPoint");
		NBTTagList list2 = (NBTTagList)tag.getTag("secondPoint");
		
		for(int i = 0; i < list1.tagCount(); ++i) {
			NBTTagCompound tag1 = list1.getCompoundTagAt(i);
			int dim = tag1.getInteger("dim");
			pos1.put(dim, BlockPos.fromLong(tag1.getLong("pos")));
		}
		
		for(int i = 0; i < list2.tagCount(); ++i) {
			NBTTagCompound tag2 = list2.getCompoundTagAt(i);
			int dim = tag2.getInteger("dim");
			pos2.put(dim, BlockPos.fromLong(tag2.getLong("pos")));
		}
		
		this.username = tag.getString("username");
		this.actionStorage.readFromNBT(tag.getCompoundTag("actionStorage"));
		return this;
	}
	
	public int getMinX() {
		int x1 = getFirstPoint().getX();
		int x2 = getSecondPoint().getX();
		return x1 < x2 ? x1 : x2;
	}
	public int getMinY() {
		int y1 = getFirstPoint().getY();
		int y2 = getSecondPoint().getY();
		return y1 < y2 ? y1 : y2;
	}
	public int getMinZ() {
		int z1 = getFirstPoint().getZ();
		int z2 = getSecondPoint().getZ();
		return z1 < z2 ? z1 : z2;
	}
	public int getMaxX() {
		int x1 = getFirstPoint().getX();
		int x2 = getSecondPoint().getX();
		return x1 > x2 ? x1 : x2;
	}
	public int getMaxY() {
		int y1 = getFirstPoint().getY();
		int y2 = getSecondPoint().getY();
		return y1 > y2 ? y1 : y2;
	}
	public int getMaxZ() {
		int z1 = getFirstPoint().getZ();
		int z2 = getSecondPoint().getZ();
		return z1 > z2 ? z1 : z2;
	}
}
