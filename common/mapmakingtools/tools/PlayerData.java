package mapmakingtools.tools;

import java.util.Hashtable;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.packet.PacketSetPoint1;
import mapmakingtools.network.packet.PacketSetPoint2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class PlayerData {

	private Hashtable<Integer, SelectedPoint> pos1 = new Hashtable<Integer, SelectedPoint>();
	private Hashtable<Integer, SelectedPoint> pos2 = new Hashtable<Integer, SelectedPoint>();
	private ActionStorage actionStorage = new ActionStorage(this);
	
	public EntityPlayer player;
	public String username;
	
	public PlayerData() {}
	public PlayerData(EntityPlayer player) {
		this.player = player;
		this.username = player.getCommandSenderName();
		this.actionStorage = new ActionStorage(this, player);
	}
	
	public void setPlayer(EntityPlayer player) {
		this.player = player;
		this.actionStorage.setPlayer(player);
	}
	
	public SelectedPoint getFirstPoint() {
		int id = player.worldObj.provider.dimensionId;		
		if(!pos1.keySet().contains(id))
			pos1.put(id, new SelectedPoint(-1));
		return pos1.get(id);
	}
	
	public SelectedPoint getSecondPoint() {
		int id = player.worldObj.provider.dimensionId;
		if(!pos2.keySet().contains(id))
			pos2.put(id, new SelectedPoint(-1));
		return pos2.get(id);
	}
	
	public int getBlockCount() {
		return (getMaxX() - getMinX() + 1) * (getMaxZ() - getMinZ() + 1) * (getMaxY() - getMinY() + 1);
	}
	
	public boolean hasSelectedPoints() {
		return getFirstPoint().getY() != -1 && getSecondPoint().getY() != -1;
	}
	
	public boolean setFirstPoint(int x, int y, int z) {
		if(y < 0 || y > 256) return false;
		getFirstPoint().setPoint(x, y, z);
		return true;
	}
	
	public boolean setSecondPoint(int x, int y, int z) {
		if(y < 0 || y > 256) return false;
		getSecondPoint().setPoint(x, y, z);
		return true;
	}
	
	public void sendUpdateToClient() {
		MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketSetPoint1(this.getFirstPoint().getX(), this.getFirstPoint().getY(), this.getFirstPoint().getZ()), this.player);
		MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketSetPoint2(this.getSecondPoint().getX(), this.getSecondPoint().getY(), this.getSecondPoint().getZ()), this.player);
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
			tag1.setTag("point", pos1.get(key).writeToNBT(new NBTTagCompound()));
			list1.appendTag(tag1);
		}
		
		for(int key : this.pos2.keySet()) {
			NBTTagCompound tag2 = new NBTTagCompound();
			tag2.setInteger("dim", key);
			tag2.setTag("point", pos2.get(key).writeToNBT(new NBTTagCompound()));
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
			NBTTagCompound tag1 = list1.func_150305_b(i);
			int dim = tag1.getInteger("dim");
			pos1.put(dim, new SelectedPoint(-1).readFromNBT(tag1.getCompoundTag("point")));
		}
		
		for(int i = 0; i < list2.tagCount(); ++i) {
			NBTTagCompound tag2 = list2.func_150305_b(i);
			int dim = tag2.getInteger("dim");
			pos2.put(dim, new SelectedPoint(-1).readFromNBT(tag2.getCompoundTag("point")));
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
