package mapmakingtools.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mapmakingtools.ModItems;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.network.packet.PacketUpdateEntity;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
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
		ItemStack stack = player.getHeldItem();
		World world = player.worldObj;
		BlockPos pos = new BlockPos(event.x, event.y, event.z);
		EnumFacing side = null;
		if(event.face != - 1)
			side = EnumFacing.getFront(event.face);
		
		//if(!world.isRemote) {
		//	player.addChatMessage(new ChatComponentTranslation("%s", "" + world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))));
			//event.setCanceled(true);
		//}
		
		switch(event.action) {
		case LEFT_CLICK_BLOCK:
			if(PlayerAccess.canEdit(player) && !world.isRemote) {
				
				//Quick Build - Left Click
				if(stack != null && stack.getItem() == ModItems.editItem && stack.getItemDamage() == 0) {
					PlayerData playerData = WorldData.getPlayerData(player);
					
					BlockPos movedPos = pos;
					if(player.isSneaking())
						movedPos = pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
	
					
					if(playerData.setFirstPoint(movedPos)) {
						if(playerData.hasSelectedPoints()) {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
							chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
							player.addChatMessage(chatComponent);
						}
						else {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
							chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
							player.addChatMessage(chatComponent);
						}
						playerData.sendUpdateToClient();
						event.setCanceled(true);
					}
				}
			}
			break;
			
		case RIGHT_CLICK_BLOCK:
			if(PlayerAccess.canEdit(player) && !world.isRemote) {
				
				//Quick Build - Right Click
				if(stack != null && stack.getItem() == ModItems.editItem && stack.getItemDamage() == 0) {
					PlayerData playerData = WorldData.getPlayerData(player);
					
					BlockPos movedPos = pos;
					if(player.isSneaking())
						movedPos = pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
					
					if(playerData.setSecondPoint(movedPos)) {
						if(playerData.hasSelectedPoints()) {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
							chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
							player.addChatMessage(chatComponent);
						}
						else {
							ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
							chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
							player.addChatMessage(chatComponent);
						}
						playerData.sendUpdateToClient();
						event.setCanceled(true);
					}
				}
				else if(stack != null && stack.getItem() == ModItems.editItem && stack.getItemDamage() == 1) {
					if(!world.isRemote) {
						TileEntity tileEntity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
						if(tileEntity != null) {
							if(tileEntity instanceof TileEntityMobSpawner) 
								SpawnerUtil.confirmHasRandomMinecart(((TileEntityMobSpawner)tileEntity).func_145881_a());
							
							
							PacketDispatcher.sendTo(new PacketUpdateBlock(tileEntity, pos), player);
							event.setCanceled(true);
						}
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
		ItemStack stack = player.getHeldItem();
		World world = player.worldObj;
		Entity entity = event.target;
		
		if(stack != null && stack.getItem() == ModItems.editItem && stack.getItemDamage() == 1) {
			if(!world.isRemote) {
				PacketDispatcher.sendTo(new PacketUpdateEntity(entity), player);
				event.setCanceled(true);
			}
		}
	}

}
