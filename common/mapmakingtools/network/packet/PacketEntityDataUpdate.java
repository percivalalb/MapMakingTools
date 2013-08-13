package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketEntityDataUpdate extends PacketMMT {

	public int entityId;
	public NBTTagCompound nbtData;
	
	public PacketEntityDataUpdate() {
		super(PacketTypeHandler.ENTITY_DATA_UPDATE, false);
	}
	
	public PacketEntityDataUpdate(Entity entity) {
		this();
		this.entityId = entity.entityId;
		NBTTagCompound tag = new NBTTagCompound(); //Creates a new instance to storage the NBT data
		entity.writeToNBT(tag); //Writes all the data about the entity to NBT
		this.nbtData = tag; //Sets the class variable as the newly created NBT
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
		this.nbtData = Packet.readNBTTagCompound(data);
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(entityId);
		writeNBTTagCompound(nbtData, dos); //Just like #Packet.writeNBTTagCompound
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			if(entityId != 0) {
	        	World world = player.worldObj;
	        	Entity entity = world.getEntityByID(entityId);
	        	if(entity != null) {
	        		entity.readFromNBT(nbtData);
	        		LogHelper.logDebug("Set entity NBT data from server");
	        	}
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}

	protected static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException {
	    if (par0NBTTagCompound == null) {
	        par1DataOutput.writeShort(-1);
	    }
	    else {
	    	byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
	        par1DataOutput.writeShort((short)abyte.length);
	        par1DataOutput.write(abyte);
	    }
	}
}
