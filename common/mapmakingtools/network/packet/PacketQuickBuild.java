package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketQuickBuild extends PacketMMT {

	public int x, y, z;
	
	public PacketQuickBuild() {
		super(PacketTypeHandler.QUICK_BUILD, false);
	}
	
	public PacketQuickBuild(int x, int y, int z) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			DataStorage.setPlayerRightClick(player, x, y, z);
			String message = "Postion 2 set at (" + x + ", " + y + ", " + z + ")";
			if(DataStorage.hasSelectedPostions(player)) {
				int secMinX = DataStorage.getSelectedPosFromPlayer(player)[0];
				int secMinY = DataStorage.getSelectedPosFromPlayer(player)[1];
				int secMinZ = DataStorage.getSelectedPosFromPlayer(player)[2];
				int secMaxX = x;
				int secMaxY = y;
				int secMaxZ = z;
				int minX = MathHelper.small(secMinX, secMaxX);
				int minY = MathHelper.small(secMinY, secMaxY);
				int minZ = MathHelper.small(secMinZ, secMaxZ);
				int maxX = MathHelper.big(secMinX, secMaxX);
				int maxY = MathHelper.big(secMinY, secMaxY);
				int maxZ = MathHelper.big(secMinZ, secMaxZ);
				int blocks = 0;
				for(int x = minX; x <= maxX; ++x) {
					for(int y = minY; y <= maxY; ++y) {
						for(int z = minZ; z <= maxZ; ++z) {
							++blocks;
						}
					}
				}
				message += EnumChatFormatting.GREEN + " " + blocks + " block(s) selected.";
			}
			else {
				message += EnumChatFormatting.RED + " 0 block(s) selected.";
			}
			
			player.addChatMessage(message);
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}
}
