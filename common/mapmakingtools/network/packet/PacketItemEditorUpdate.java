package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class PacketItemEditorUpdate extends PacketMMT {
	
	public int slotNo;
	public ItemStack stack;
	
	public PacketItemEditorUpdate() {
		super(PacketTypeHandler.ITEM_EDITOR_UPDATE, false);
	}
	
	public PacketItemEditorUpdate(ItemStack stack, int slotNo) {
		this();
		this.stack = stack;
		this.slotNo = slotNo;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.slotNo = data.readInt();
		this.stack = Packet.readItemStack(data);
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(slotNo);
		Packet.writeItemStack(stack, dos);
	}
	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			player.inventory.setInventorySlotContents(slotNo, stack);
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
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
