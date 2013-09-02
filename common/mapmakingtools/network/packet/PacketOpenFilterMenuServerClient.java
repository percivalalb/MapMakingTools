package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import mapmakingtools.MapMakingTools;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.proxy.CommonProxy;
import mapmakingtools.core.util.WrenchTasks;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketOpenFilterMenuServerClient extends PacketMMT {
	
	public enum Mode {
		ENTITY,
		BLOCK;
	}
	
	public int x, y, z;
	public int entityId;
	public NBTTagCompound tagCompound;
	public Mode mode = Mode.BLOCK;
	
	public PacketOpenFilterMenuServerClient() {
		super(PacketTypeHandler.FITLER_MENU_NBT_UPDATE, false);
	}
	
	public PacketOpenFilterMenuServerClient(World world, int x, int y, int z) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null) {
			this.tagCompound = new NBTTagCompound();
			tile.writeToNBT(this.tagCompound);
		}
		this.mode = Mode.BLOCK;
	}
	
	public PacketOpenFilterMenuServerClient(World world, int entityId) {
		this();
		this.entityId = entityId;
		Entity entity = world.getEntityByID(entityId);
		if(entity != null) {
			this.tagCompound = new NBTTagCompound();
			entity.writeToNBT(this.tagCompound);
		}
		this.mode = Mode.ENTITY;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.mode = data.readBoolean() ? Mode.BLOCK : Mode.ENTITY;
		if(mode == Mode.ENTITY) {
			this.entityId = data.readInt();
		}
		else {
			this.x = data.readInt();
			this.y = data.readInt();
			this.z = data.readInt();
		}
		if(data.readBoolean()) 
			this.tagCompound = Packet.readNBTTagCompound(data);
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeBoolean(mode == Mode.BLOCK);
		if(mode == Mode.ENTITY) {
			dos.writeInt(entityId);
		}
		else {
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
		}
		dos.writeBoolean(this.tagCompound != null);
		if(this.tagCompound != null)
			this.writeNBTTagCompound(tagCompound, dos);
	}
	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			if(mode == Mode.ENTITY) {
				Entity entity = player.worldObj.getEntityByID(entityId);
				if(entity != null && tagCompound != null) 
					entity.readFromNBT(tagCompound);
			}
			else {
				TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
				if(tile != null && tagCompound != null) 
					tile.readFromNBT(tagCompound);
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}

	protected static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException
    {
        if (par0NBTTagCompound == null)
        {
            par1DataOutput.writeShort(-1);
        }
        else
        {
            byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
            par1DataOutput.writeShort((short)abyte.length);
            par1DataOutput.write(abyte);
        }
    }
}
