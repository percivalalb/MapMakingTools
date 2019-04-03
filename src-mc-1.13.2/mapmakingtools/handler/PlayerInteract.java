package mapmakingtools.handler;

import mapmakingtools.ModItems;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.network.packet.PacketUpdateEntity;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class PlayerInteract {

	@SubscribeEvent
	public void onRightClick(RightClickBlock event) {
		
		EntityPlayer player = event.getEntityPlayer();
		EnumHand hand = event.getHand();
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		World world = player.world;
		BlockPos pos = event.getPos();
		EnumFacing side = event.getFace();
		
		//Is Client world or is doing off hand calculation
		if(world.isRemote || hand.equals(EnumHand.OFF_HAND)) {
			if(PlayerAccess.canEdit(player)) {
				if(stack.getItem() == ModItems.EDIT_ITEM) {
					event.setCanceled(true);
				}
			}
		}
		else if(PlayerAccess.canEdit(player)) {
			if(stack.getItem() == ModItems.EDIT_ITEM && stack.getMetadata() == 0) {
				PlayerData playerData = WorldData.getPlayerData(player);
					
				BlockPos movedPos = player.isSneaking() ? pos.offset(side) : pos;
					
				if(playerData.setSecondPoint(movedPos)) {
					if(playerData.hasSelectedPoints()) {
						TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
						chatComponent.getStyle().setColor(TextFormatting.GREEN);
						player.sendMessage(chatComponent);
					}
					else {
						TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
						chatComponent.getStyle().setColor(TextFormatting.RED);
						player.sendMessage(chatComponent);
					}
					playerData.sendUpdateToClient();
					event.setCanceled(true);
				}
			}
			else if(stack.getItem() == ModItems.EDIT_ITEM && stack.getMetadata() == 1) {
				TileEntity tileEntity = world.getTileEntity(pos);
				if(tileEntity != null) {
							
					if(tileEntity instanceof TileEntityMobSpawner) {
						MobSpawnerBaseLogic spawnerLogic = ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic();
						SpawnerUtil.confirmHasRandomMinecart(spawnerLogic);
					}
							
					//player.openGui(MapMakingTools.INSTANCE, CommonProxy.ID_FILTER_BLOCK, player.world, pos.getX(), pos.getY(), pos.getZ());		
					PacketDispatcher.sendTo(new PacketUpdateBlock(tileEntity, pos, false), player);
					event.setCanceled(true);
				}
			}	
		}
	}
	
	@SubscribeEvent
	public void onLeftClick(LeftClickBlock event) {
		
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		World world = player.world;
		BlockPos pos = event.getPos();
		EnumFacing side = event.getFace();

		//Is Client world or is doing off hand calculation
		if(world.isRemote || event.getHand().equals(EnumHand.OFF_HAND)) {
			if(PlayerAccess.canEdit(player)) {
				if(stack.getItem() == ModItems.EDIT_ITEM && stack.getMetadata() == 0) {
					event.setCanceled(true);
				}
			}
		}
		else if(PlayerAccess.canEdit(player)) {
			if(stack.getItem() == ModItems.EDIT_ITEM && stack.getMetadata() == 0) {
				PlayerData playerData = WorldData.getPlayerData(player);
					
				BlockPos movedPos = player.isSneaking() ? pos.offset(side) : pos;
					
				if(playerData.setFirstPoint(movedPos)) {
					if(playerData.hasSelectedPoints()) {
						TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
						chatComponent.getStyle().setColor(TextFormatting.GREEN);
						player.sendMessage(chatComponent);
					}
					else {
						TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
						chatComponent.getStyle().setColor(TextFormatting.RED);
						player.sendMessage(chatComponent);
					}
					playerData.sendUpdateToClient();
					event.setCanceled(true);
				}
			}
		}
	
	}
	
	@SubscribeEvent
	public void entityInteract(EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getItemStack();
		World world = player.world;
		Entity entity = event.getTarget();
		
		if(stack.getItem() == ModItems.EDIT_ITEM && stack.getMetadata() == 1) {
			if(!world.isRemote) {
				PacketDispatcher.sendTo(new PacketUpdateEntity(entity, false), player);
				event.setCanceled(true);
			}
		}
	}

}
