package mapmakingtools.handler;

import mapmakingtools.ModItems;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;

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
			if(stack.getItem() == ModItems.EDIT_ITEM) {
				PlayerData playerData = WorldData.getPlayerData(player);
					
				BlockPos movedPos = player.isSneaking() ? pos.offset(side) : pos;
					
				if(playerData.setSecondPoint(movedPos)) {
					if(playerData.hasSelectedPoints()) {
						player.sendMessage(new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount()).applyTextStyle(TextFormatting.GREEN));
					}
					else {
						player.sendMessage(new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative").applyTextStyle(TextFormatting.RED));
					}
					playerData.sendUpdateToClient(player);
					event.setCanceled(true);
				}
			}
			else if(stack.getItem() == ModItems.WRENCH) {
				TileEntity tileEntity = world.getTileEntity(pos);
				if(tileEntity != null) {
							
					if(tileEntity instanceof TileEntityMobSpawner) {
						MobSpawnerBaseLogic spawnerLogic = ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic();
						SpawnerUtil.confirmHasRandomMinecart(spawnerLogic);
					}
					
					if(player instanceof EntityPlayerMP && !(player instanceof FakePlayer)) {
	                    EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;

	                    NetworkHooks.openGui(entityPlayerMP, new IInteractionObject() {

							@Override
							public ITextComponent getCustomName() { return null; }

							@Override
							public ITextComponent getName() {
								return new TextComponentString("mapmakingtools.filter.block");
							}

							@Override
							public boolean hasCustomName() {
								return false;
							}

							@Override
							public Container createContainer(InventoryPlayer inventory, EntityPlayer player) {
								return new ContainerFilter(FilterManager.getServerBlocksFilters(player, player.world, pos), player);
							}

							@Override
							public String getGuiID() {
								return "mapmakingtools:filter";
							}
	                    	
	                    }, buf -> {
	                    	buf.writeByte(1);
	                    	buf.writeBlockPos(pos);
	                    });
	                }
					
					//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(tileEntity, pos, false), player);
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
				if(stack.getItem() == ModItems.EDIT_ITEM) {
					event.setCanceled(true);
				}
			}
		}
		else if(PlayerAccess.canEdit(player)) {
			if(stack.getItem() == ModItems.EDIT_ITEM) {
				PlayerData playerData = WorldData.getPlayerData(player);
					
				BlockPos movedPos = player.isSneaking() ? pos.offset(side) : pos;
					
				if(playerData.setFirstPoint(movedPos)) {
					if(playerData.hasSelectedPoints()) {
						player.sendMessage(new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount()).applyTextStyle(TextFormatting.GREEN));
					}
					else {
						player.sendMessage(new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative").applyTextStyle(TextFormatting.RED));
					}
					playerData.sendUpdateToClient(player);
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
		
		if(stack.getItem() == ModItems.WRENCH) {
			if(!world.isRemote) {
				if(player instanceof EntityPlayerMP && !(player instanceof FakePlayer)) {
                    EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;

                    NetworkHooks.openGui(entityPlayerMP, new IInteractionObject() {

						@Override
						public ITextComponent getCustomName() { return null; }

						@Override
						public ITextComponent getName() {
							return new TextComponentString("mapmakingtools.filter.entity");
						}

						@Override
						public boolean hasCustomName() {
							return false;
						}

						@Override
						public Container createContainer(InventoryPlayer inventory, EntityPlayer player) {
							return new ContainerFilter(FilterManager.getServerEntitiesFilters(player, entity), player);
						}

						@Override
						public String getGuiID() {
							return "mapmakingtools:filter";
						}
                    	
                    }, buf -> {
                    	buf.writeByte(0);
                    	buf.writeInt(entity.getEntityId());
                    });
                }
				
				//TODO PacketDispatcher.sendTo(new PacketUpdateEntity(entity, false), player);
				event.setCanceled(true);
			}
		}
	}

}
