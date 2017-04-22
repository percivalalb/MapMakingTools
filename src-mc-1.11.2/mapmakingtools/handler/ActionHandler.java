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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class ActionHandler {

	@SubscribeEvent
	public void onRightClick(RightClickBlock event) {
		
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getItemStack();
		World world = player.world;
		BlockPos pos = event.getPos();
		EnumFacing side = event.getFace();
		
		//if(!world.isRemote) {
		//	player.sendMessage(new TextComponentTranslation("%s", "" + world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))));
			//event.setCanceled(true);
		//}
		

			if(PlayerAccess.canEdit(player) && !world.isRemote) {
				
				//Quick Build - Right Click
				if(stack.getItem() == ModItems.editItem && stack.getMetadata() == 0) {
					PlayerData playerData = WorldData.getPlayerData(player);
					
					BlockPos movedPos = pos;
					if(player.isSneaking())
						movedPos = pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
					
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
				else if(stack.getItem() == ModItems.editItem && stack.getMetadata() == 1) {
					if(!world.isRemote) {
						TileEntity tileEntity = world.getTileEntity(pos);
						if(tileEntity != null) {
							
							if(tileEntity instanceof TileEntityMobSpawner) {
								MobSpawnerBaseLogic spawnerLogic = ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic();
								
								FMLLog.info("ewaeaw e" + SpawnerUtil.getPotentialSpawns(spawnerLogic).size());
								SpawnerUtil.confirmHasRandomMinecart(spawnerLogic);
								FMLLog.info("ewaeaw e" + SpawnerUtil.getPotentialSpawns(spawnerLogic).size());
							
							}
							
							
							PacketDispatcher.sendTo(new PacketUpdateBlock(tileEntity, pos, false), player);
							//SpawnerUtil.getTileEntitySpawnerPacket(spawner)
							event.setCanceled(true);
						}
					}
				}
			}

	}
	
	@SubscribeEvent
	public void onLeftClick(LeftClickBlock event) {
		
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getItemStack();
		World world = player.world;
		BlockPos pos = event.getPos();
		EnumFacing side = event.getFace();

			if(PlayerAccess.canEdit(player) && !world.isRemote) {
				
				//Quick Build - Left Click
				if(stack.getItem() == ModItems.editItem && stack.getMetadata() == 0) {
					PlayerData playerData = WorldData.getPlayerData(player);
					
					BlockPos movedPos = pos;
					if(player.isSneaking())
						movedPos = pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
	
					
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
		
		if(stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 1) {
			if(!world.isRemote) {
				PacketDispatcher.sendTo(new PacketUpdateEntity(entity), player);
				event.setCanceled(true);
			}
		}
	}

}
