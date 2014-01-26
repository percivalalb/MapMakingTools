package mapmakingtools.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mapmakingtools.item.ItemEdit;
import mapmakingtools.network.ChannelOutBoundHandler;
import mapmakingtools.network.packet.PacketSetPoint1;
import mapmakingtools.network.packet.PacketSetPoint2;
import mapmakingtools.network.packet.PacketTest;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.network.packet.PacketUpdateEntity;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author ProPercivalalb
 */
public class ActionHandler {

	@SubscribeEvent
	public void rightClick(PlayerInteractEvent event) {
		EntityPlayer player = event.entityPlayer;
		ItemStack item = player.getHeldItem();
		World world = player.worldObj;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		int side = event.face;
		if(world.isRemote)
			ChannelOutBoundHandler.sendPacketToServer(new PacketTest());
		
		switch(event.action) {
		case LEFT_CLICK_BLOCK:
			if(PlayerAccess.canEdit(player) && !world.isRemote) {
				
				//Quick Build - Left Click
				if(ItemEdit.isAxe(item) && !ItemEdit.isWrench(item)) {
					PlayerData playerData = WorldData.getPlayerData(player);
					
					x += (player.isSneaking() ? Facing.offsetsXForSide[side] : 0);
					y += (player.isSneaking() ? Facing.offsetsYForSide[side] : 0);
					z += (player.isSneaking() ? Facing.offsetsZForSide[side] : 0);
					
					if(playerData.setFirstPoint(x, y, z)) {
						ChannelOutBoundHandler.sendPacketToClient(player, new PacketSetPoint1(x, y, z));
						if(playerData.hasSelectedPoints()) {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
							chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
							player.func_145747_a(chatComponent);
						}
						else {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
							chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.RED);
							player.func_145747_a(chatComponent);
						}
						event.setCanceled(true);
					}
				}
			}
			break;
			
		case RIGHT_CLICK_BLOCK:
			if(PlayerAccess.canEdit(player) && !world.isRemote) {
				
				//Quick Build - Right Click
				if(ItemEdit.isAxe(item) && !ItemEdit.isWrench(item)) {
					PlayerData playerData = WorldData.getPlayerData(player);
					
					x += (player.isSneaking() ? Facing.offsetsXForSide[side] : 0);
					y += (player.isSneaking() ? Facing.offsetsYForSide[side] : 0);
					z += (player.isSneaking() ? Facing.offsetsZForSide[side] : 0);
					
					if(playerData.setSecondPoint(x, y, z)) {
						ChannelOutBoundHandler.sendPacketToClient(player, new PacketSetPoint2(x, y, z));
						if(playerData.hasSelectedPoints()) {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
							chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
							player.func_145747_a(chatComponent);
						}
						else {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
							chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.RED);
							player.func_145747_a(chatComponent);
						}
						event.setCanceled(true);
					}
				}
				else if(ItemEdit.isAxe(item) && ItemEdit.isWrench(item)) {
					TileEntity tileEntity = world.func_147438_o(x, y, z);
					if(tileEntity != null) {
						ChannelOutBoundHandler.sendPacketToClient(player, new PacketUpdateBlock(tileEntity, x, y, z));
						event.setCanceled(true);
					}
				}
			}
			break;
			
		case RIGHT_CLICK_AIR:
			break;
		default:
			break;
		
		}
	}
	
	@SubscribeEvent
	public void entityInteract(EntityInteractEvent event) {
		EntityPlayer player = event.entityPlayer;
		ItemStack item = player.getHeldItem();
		World world = player.worldObj;
		Entity entity = event.target;
		
		if(ItemEdit.isAxe(item) && ItemEdit.isWrench(item)) {
			if(!world.isRemote) {
				ChannelOutBoundHandler.sendPacketToClient(player, new PacketUpdateEntity(entity));
				event.setCanceled(true);
			}
		}
	}

}
