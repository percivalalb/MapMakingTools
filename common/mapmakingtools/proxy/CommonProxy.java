package mapmakingtools.proxy;

import java.util.List;

import mapmakingtools.api.FilterManager;
import mapmakingtools.api.FlippedManager;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IFilterServer;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.ContainerItemEditor;
import mapmakingtools.tools.filter.CommandBlockAliasClientFilter;
import mapmakingtools.tools.filter.CommandBlockAliasServerFilter;
import mapmakingtools.tools.filter.CustomGiveClientFilter;
import mapmakingtools.tools.filter.CustomGiveServerFilter;
import mapmakingtools.tools.filter.FillInventoryClientFilter;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import mapmakingtools.tools.filter.ItemSpawnerClientFilter;
import mapmakingtools.tools.filter.ItemSpawnerServerFilter;
import mapmakingtools.tools.filter.MobArmorClientFilter;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.tools.filter.MobPositionClientFilter;
import mapmakingtools.tools.filter.MobPositionServerFilter;
import mapmakingtools.tools.filter.MobTypeClientFilter;
import mapmakingtools.tools.filter.MobTypeServerFilter;
import mapmakingtools.tools.filter.SpawnerFilterProvider;
import mapmakingtools.tools.filter.SpawnerTimingClientFilter;
import mapmakingtools.tools.filter.SpawnerTimingServerFilter;
import mapmakingtools.tools.flipped.FlippedButton;
import mapmakingtools.tools.flipped.FlippedChest;
import mapmakingtools.tools.flipped.FlippedDispenser;
import mapmakingtools.tools.flipped.FlippedDropper;
import mapmakingtools.tools.flipped.FlippedFenceGate;
import mapmakingtools.tools.flipped.FlippedFurnace;
import mapmakingtools.tools.flipped.FlippedHopper;
import mapmakingtools.tools.flipped.FlippedLadder;
import mapmakingtools.tools.flipped.FlippedPoweredRail;
import mapmakingtools.tools.flipped.FlippedPumpkin;
import mapmakingtools.tools.flipped.FlippedRedstoneComparator;
import mapmakingtools.tools.flipped.FlippedRedstoneRepeater;
import mapmakingtools.tools.flipped.FlippedSignPost;
import mapmakingtools.tools.flipped.FlippedSignWall;
import mapmakingtools.tools.flipped.FlippedStairs;
import mapmakingtools.tools.flipped.FlippedStoneSlab;
import mapmakingtools.tools.flipped.FlippedTorch;
import mapmakingtools.tools.flipped.FlippedTrapdoor;
import mapmakingtools.tools.flipped.FlippedTripwireSource;
import mapmakingtools.tools.flipped.FlippedVanillaTrack;
import mapmakingtools.tools.flipped.FlippedWoodenSlab;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * @author ProPercivalalb
 */
public class CommonProxy implements IGuiHandler {

	public static final int ID_FILTER_BLOCK = 0;
	public static final int ID_FILTER_ENTITY = 1;
	public static final int GUI_ID_ITEM_EDITOR = 2;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == ID_FILTER_BLOCK) {
			List<IFilterServer> filterList = FilterManager.getServerBlocksFilters(player, world, x, y, z);
			if(filterList.size() > 0)
				return new ContainerFilter(filterList, player).setBlockCoords(x, y, z);
		}
		else if(ID == ID_FILTER_ENTITY) {
			List<IFilterServer> filterList = FilterManager.getServerEntitiesFilters(player, world.getEntityByID(x));
			if(filterList.size() > 0)
				return new ContainerFilter(filterList, player).setEntityId(x);
		}
		else if(ID == GUI_ID_ITEM_EDITOR) {
			return new ContainerItemEditor(player, x);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) { 
		if(ID == ID_FILTER_BLOCK) {
			List<IFilterClient> filterList = FilterManager.getClientBlocksFilters(player, world, x, y, z);
			if(filterList.size() > 0)
				return new GuiFilter(filterList, player, x, y, z);
		}
		else if(ID == ID_FILTER_ENTITY) {
			List<IFilterClient> filterList = FilterManager.getClientEntitiesFilters(player, world.getEntityByID(x));
			if(filterList.size() > 0)
				return new GuiFilter(filterList, player, x);
		}
		else if(ID == GUI_ID_ITEM_EDITOR) {
			return new GuiItemEditor(player, x);
		}
		return null;
	}

	public void registerHandlers() {}
	public void onPreLoad() {}
	public EntityPlayer getClientPlayer() { return null; }
	
	
	public void registerFilters() {
    	FilterManager.registerFilter(FillInventoryClientFilter.class, FillInventoryServerFilter.class);
    	FilterManager.registerFilter(MobTypeClientFilter.class, MobTypeServerFilter.class);
    	FilterManager.registerFilter(MobPositionClientFilter.class, MobPositionServerFilter.class);
    	FilterManager.registerFilter(MobArmorClientFilter.class, MobArmorServerFilter.class);
    	FilterManager.registerFilter(CommandBlockAliasClientFilter.class, CommandBlockAliasServerFilter.class);
    	FilterManager.registerFilter(CustomGiveClientFilter.class, CustomGiveServerFilter.class);
    	FilterManager.registerFilter(ItemSpawnerClientFilter.class, ItemSpawnerServerFilter.class);
    	FilterManager.registerFilter(SpawnerTimingClientFilter.class, SpawnerTimingServerFilter.class);
    	
    	FilterManager.registerProvider(SpawnerFilterProvider.class);
	}

	public void registerFlipped() {
		FlippedManager.registerFlippedHandler(Blocks.stone_slab, new FlippedStoneSlab());
		FlippedManager.registerFlippedHandler(Blocks.wooden_slab, new FlippedWoodenSlab());
		FlippedManager.registerFlippedHandler(Blocks.unpowered_repeater, new FlippedRedstoneRepeater());
		FlippedManager.registerFlippedHandler(Blocks.powered_repeater, new FlippedRedstoneRepeater());
		FlippedManager.registerFlippedHandler(Blocks.unpowered_comparator, new FlippedRedstoneComparator());
		FlippedManager.registerFlippedHandler(Blocks.powered_comparator, new FlippedRedstoneComparator());
		FlippedManager.registerFlippedHandler(Blocks.chest, new FlippedChest());
		FlippedManager.registerFlippedHandler(Blocks.trapped_chest, new FlippedChest());
		FlippedManager.registerFlippedHandler(Blocks.ender_chest, new FlippedChest());
		FlippedManager.registerFlippedHandler(Blocks.hopper, new FlippedHopper());
		FlippedManager.registerFlippedHandler(Blocks.torch, new FlippedTorch());
		FlippedManager.registerFlippedHandler(Blocks.unlit_redstone_torch, new FlippedTorch());
		FlippedManager.registerFlippedHandler(Blocks.redstone_torch, new FlippedTorch());
		FlippedManager.registerFlippedHandler(Blocks.fence_gate, new FlippedFenceGate());
		FlippedManager.registerFlippedHandler(Blocks.ladder, new FlippedLadder());
		FlippedManager.registerFlippedHandler(Blocks.furnace, new FlippedFurnace());
		FlippedManager.registerFlippedHandler(Blocks.lit_furnace, new FlippedFurnace());
		FlippedManager.registerFlippedHandler(Blocks.dropper, new FlippedDropper());
		FlippedManager.registerFlippedHandler(Blocks.dispenser, new FlippedDispenser());
		FlippedManager.registerFlippedHandler(Blocks.trapdoor, new FlippedTrapdoor());
		FlippedManager.registerFlippedHandler(Blocks.pumpkin, new FlippedPumpkin());
		FlippedManager.registerFlippedHandler(Blocks.lit_pumpkin, new FlippedPumpkin());
		FlippedManager.registerFlippedHandler(Blocks.wooden_button, new FlippedButton());
		FlippedManager.registerFlippedHandler(Blocks.stone_button, new FlippedButton());
		FlippedManager.registerFlippedHandler(Blocks.tripwire_hook, new FlippedTripwireSource());
		FlippedManager.registerFlippedHandler(Blocks.oak_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.spruce_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.birch_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.jungle_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.acacia_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.dark_oak_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.brick_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.stone_brick_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.stone_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.nether_brick_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.sandstone_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.quartz_stairs, new FlippedStairs());
		FlippedManager.registerFlippedHandler(Blocks.standing_sign, new FlippedSignPost());
		FlippedManager.registerFlippedHandler(Blocks.wall_sign, new FlippedSignWall());
		FlippedManager.registerFlippedHandler(Blocks.rail, new FlippedVanillaTrack());
		FlippedManager.registerFlippedHandler(Blocks.golden_rail, new FlippedPoweredRail());
		FlippedManager.registerFlippedHandler(Blocks.detector_rail, new FlippedPoweredRail());
		FlippedManager.registerFlippedHandler(Blocks.activator_rail, new FlippedPoweredRail());
		//TODO FlippedManager.registerFlippedHandler(Block.bed, new FlippedBed());
	}
}
