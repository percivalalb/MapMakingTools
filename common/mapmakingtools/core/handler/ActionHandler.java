package mapmakingtools.core.handler;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import mapmakingtools.MapMakingTools;
import mapmakingtools.ModItems;
import mapmakingtools.api.IFilter;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.GuiBeacon;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiSkull;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.PlayerHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.proxy.CommonProxy;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.Constants;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketBabyMonster;
import mapmakingtools.network.packet.PacketEntityDataUpdate;
import mapmakingtools.network.packet.PacketOpenFilterMenu;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

/**
 * @author ProPercivalalb
 */
public class ActionHandler {

	@ForgeSubscribe
	public void rightClick(PlayerInteractEvent event) {
		//LogHelper.logDebug("Metadata: " + event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z));
		
		if(event.entityPlayer.capabilities.isCreativeMode && event.action == Action.LEFT_CLICK_BLOCK && ItemStackHelper.isItem(event.entityPlayer.getHeldItem(), Constants.QUICK_BUILD_ITEM)) {
			if(!event.entityPlayer.worldObj.isRemote) {
				DataStorage.setPlayerLeftClick(event.entityPlayer, event.x, event.y, event.z);
				String message = "Postion 1 set at (" + event.x + ", " + event.y + ", " + event.z + ")";
				if(DataStorage.hasSelectedPostions(event.entityPlayer)) {
					int secMinX = event.x;
					int secMinY = event.y;
					int secMinZ = event.z;
					int secMaxX = DataStorage.getSelectedPosFromPlayer(event.entityPlayer)[3];
					int secMaxY = DataStorage.getSelectedPosFromPlayer(event.entityPlayer)[4];
					int secMaxZ = DataStorage.getSelectedPosFromPlayer(event.entityPlayer)[5]; 
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
				
				event.entityPlayer.addChatMessage(message);
				event.setCanceled(true);
			}
		}
		if(event.entityPlayer.capabilities.isCreativeMode && event.action == Action.RIGHT_CLICK_BLOCK && ItemStackHelper.isItem(event.entityPlayer.getHeldItem(), Constants.QUICK_BUILD_ITEM)) {
			if(!event.entityPlayer.worldObj.isRemote) {
				DataStorage.setPlayerRightClick(event.entityPlayer, event.x, event.y, event.z);
				String message = "Postion 2 set at (" + event.x + ", " + event.y + ", " + event.z + ")";
				if(DataStorage.hasSelectedPostions(event.entityPlayer)) {
					int secMinX = DataStorage.getSelectedPosFromPlayer(event.entityPlayer)[0];
					int secMinY = DataStorage.getSelectedPosFromPlayer(event.entityPlayer)[1];
					int secMinZ = DataStorage.getSelectedPosFromPlayer(event.entityPlayer)[2];
					int secMaxX = event.x;
					int secMaxY = event.y;
					int secMaxZ = event.z;
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
				
				event.entityPlayer.addChatMessage(message);
				event.setCanceled(true);
			}
		}
		
		if(event.action != Action.LEFT_CLICK_BLOCK && ItemStackHelper.isItem(event.entityPlayer.getHeldItem(), Item.skull)) {
			if(event.entityPlayer.getHeldItem().getItemDamage() == 3 && PlayerHelper.isSneaking(event.entityPlayer)) {
				event.setCanceled(true);
			    if(event.entityPlayer.worldObj.isRemote) {
			    	FMLCommonHandler.instance().showGuiScreen(new GuiSkull(event.entityPlayer));
			    }
			}//Compiler
		}
		
		if(event.action == Action.RIGHT_CLICK_BLOCK) {
			if(ItemStackHelper.isItem(event.entityPlayer.getHeldItem(), ModItems.wrench)) {
				event.setCanceled(true);
				PacketTypeHandler.populatePacketAndSendToServer(new PacketOpenFilterMenu(event.x, event.y, event.z));
			}
		}
	}
	
	@ForgeSubscribe
	public void entityInteract(EntityInteractEvent event) {
		if(event.target == null || event.target.isDead) return;
		
		if(ItemStackHelper.isItem(event.entityPlayer.getHeldItem(), ModItems.wrench) && !event.entityPlayer.worldObj.isRemote) {
			event.setCanceled(true);
			PacketTypeHandler.populatePacketAndSendToClient(new PacketEntityDataUpdate(event.target), (EntityPlayerMP)event.entityPlayer);
		}
		if(ItemStackHelper.isItem(event.entityPlayer.getHeldItem(), ModItems.wrench) && event.entityPlayer.worldObj.isRemote) {
			PacketTypeHandler.populatePacketAndSendToServer(new PacketOpenFilterMenu(event.target.entityId));
		}
	}
}
