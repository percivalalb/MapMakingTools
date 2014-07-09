package mapmakingtools.proxy;

import java.util.List;

import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.api.manager.FlippedManager;
import mapmakingtools.api.manager.RotationManager;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.client.gui.GuiSkull;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.ContainerItemEditor;
import mapmakingtools.tools.datareader.BlockList;
import mapmakingtools.tools.datareader.BlockColourList;
import mapmakingtools.tools.datareader.ChestSymmetrifyData;
import mapmakingtools.tools.datareader.SpawnerEntitiesList;
import mapmakingtools.tools.filter.BabyMonsterClientFilter;
import mapmakingtools.tools.filter.BabyMonsterServerFilter;
import mapmakingtools.tools.filter.ChestSymmetrifyClientFilter;
import mapmakingtools.tools.filter.ChestSymmetrifyServerFilter;
import mapmakingtools.tools.filter.CommandBlockAliasClientFilter;
import mapmakingtools.tools.filter.CommandBlockAliasServerFilter;
import mapmakingtools.tools.filter.ConvertToDispenserClientFilter;
import mapmakingtools.tools.filter.ConvertToDispenserServerFilter;
import mapmakingtools.tools.filter.ConvertToDropperClientFilter;
import mapmakingtools.tools.filter.ConvertToDropperServerFilter;
import mapmakingtools.tools.filter.CreeperPropertiesClientFilter;
import mapmakingtools.tools.filter.CreeperPropertiesServerFilter;
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
import mapmakingtools.tools.filter.MobVelocityClientFilter;
import mapmakingtools.tools.filter.MobVelocityServerFilter;
import mapmakingtools.tools.filter.SpawnerFilterProvider;
import mapmakingtools.tools.filter.SpawnerTimingClientFilter;
import mapmakingtools.tools.filter.SpawnerTimingServerFilter;
import mapmakingtools.tools.filter.VillagerShopClientFilter;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
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
import mapmakingtools.tools.rotation.RotationAnvil;
import mapmakingtools.tools.rotation.RotationBed;
import mapmakingtools.tools.rotation.RotationButton;
import mapmakingtools.tools.rotation.RotationChest;
import mapmakingtools.tools.rotation.RotationDispenser;
import mapmakingtools.tools.rotation.RotationDropper;
import mapmakingtools.tools.rotation.RotationFenceGate;
import mapmakingtools.tools.rotation.RotationFurnace;
import mapmakingtools.tools.rotation.RotationHayBale;
import mapmakingtools.tools.rotation.RotationHopper;
import mapmakingtools.tools.rotation.RotationLadder;
import mapmakingtools.tools.rotation.RotationLever;
import mapmakingtools.tools.rotation.RotationPoweredRail;
import mapmakingtools.tools.rotation.RotationPumpkin;
import mapmakingtools.tools.rotation.RotationQuatzPillar;
import mapmakingtools.tools.rotation.RotationRedstoneComparator;
import mapmakingtools.tools.rotation.RotationRedstoneRepeater;
import mapmakingtools.tools.rotation.RotationSignPost;
import mapmakingtools.tools.rotation.RotationSignWall;
import mapmakingtools.tools.rotation.RotationStairs;
import mapmakingtools.tools.rotation.RotationTorch;
import mapmakingtools.tools.rotation.RotationTrapdoor;
import mapmakingtools.tools.rotation.RotationTripwireSource;
import mapmakingtools.tools.rotation.RotationVanillaLog;
import mapmakingtools.tools.rotation.RotationVanillaTrack;
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
	public static final int GUI_ID_SKULL_NAME = 3;
	
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
		else if(ID == GUI_ID_SKULL_NAME) {
			return new GuiSkull(player);
		}
		return null;
	}

	public void registerHandlers() {}
	public EntityPlayer getClientPlayer() { return null; }
	
	public void onPreLoad() {
		BlockList.readDataFromFile();
		BlockColourList.readDataFromFile();
		SpawnerEntitiesList.readDataFromFile();
		ChestSymmetrifyData.readDataFromFile();
	}
	
	public void registerFilters() {
    	FilterManager.registerFilter(FillInventoryClientFilter.class, FillInventoryServerFilter.class);
    	FilterManager.registerFilter(MobTypeClientFilter.class, MobTypeServerFilter.class);
    	FilterManager.registerFilter(MobPositionClientFilter.class, MobPositionServerFilter.class);
    	FilterManager.registerFilter(MobVelocityClientFilter.class, MobVelocityServerFilter.class);
    	FilterManager.registerFilter(BabyMonsterClientFilter.class, BabyMonsterServerFilter.class);
    	FilterManager.registerFilter(CreeperPropertiesClientFilter.class, CreeperPropertiesServerFilter.class);
    	FilterManager.registerFilter(MobArmorClientFilter.class, MobArmorServerFilter.class);
    	FilterManager.registerFilter(CommandBlockAliasClientFilter.class, CommandBlockAliasServerFilter.class);
    	FilterManager.registerFilter(CustomGiveClientFilter.class, CustomGiveServerFilter.class);
    	FilterManager.registerFilter(ItemSpawnerClientFilter.class, ItemSpawnerServerFilter.class);
    	FilterManager.registerFilter(SpawnerTimingClientFilter.class, SpawnerTimingServerFilter.class);
    	
    	FilterManager.registerFilter(ConvertToDispenserClientFilter.class, ConvertToDispenserServerFilter.class);
    	FilterManager.registerFilter(ConvertToDropperClientFilter.class, ConvertToDropperServerFilter.class);
    	
    	FilterManager.registerFilter(ChestSymmetrifyClientFilter.class, ChestSymmetrifyServerFilter.class);
    	
    	
    	FilterManager.registerFilter(VillagerShopClientFilter.class, VillagerShopServerFilter.class);
    	
    	FilterManager.registerProvider(SpawnerFilterProvider.class);
	}
	

	public void registerRotation() {
		RotationManager.registerRotationHandler(Blocks.log, new RotationVanillaLog());
		RotationManager.registerRotationHandler(Blocks.log2, new RotationVanillaLog());
		RotationManager.registerRotationHandler(Blocks.quartz_block, new RotationQuatzPillar());
		RotationManager.registerRotationHandler(Blocks.furnace, new RotationFurnace());
		RotationManager.registerRotationHandler(Blocks.lit_furnace, new RotationFurnace());
		RotationManager.registerRotationHandler(Blocks.dropper, new RotationDropper());
		RotationManager.registerRotationHandler(Blocks.dispenser, new RotationDispenser());
		RotationManager.registerRotationHandler(Blocks.chest, new RotationChest());
		RotationManager.registerRotationHandler(Blocks.trapped_chest, new RotationChest());
		RotationManager.registerRotationHandler(Blocks.ender_chest, new RotationChest());
		RotationManager.registerRotationHandler(Blocks.anvil, new RotationAnvil());
		RotationManager.registerRotationHandler(Blocks.oak_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.spruce_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.birch_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.jungle_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.acacia_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.dark_oak_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.brick_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.stone_brick_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.stone_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.nether_brick_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.sandstone_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.quartz_stairs, new RotationStairs());
		RotationManager.registerRotationHandler(Blocks.hay_block, new RotationHayBale());
		RotationManager.registerRotationHandler(Blocks.standing_sign, new RotationSignPost());
		RotationManager.registerRotationHandler(Blocks.wall_sign, new RotationSignWall());
		RotationManager.registerRotationHandler(Blocks.rail, new RotationVanillaTrack());
		RotationManager.registerRotationHandler(Blocks.golden_rail, new RotationPoweredRail());
		RotationManager.registerRotationHandler(Blocks.detector_rail, new RotationPoweredRail());
		RotationManager.registerRotationHandler(Blocks.activator_rail, new RotationPoweredRail());
		RotationManager.registerRotationHandler(Blocks.hopper, new RotationHopper());
		RotationManager.registerRotationHandler(Blocks.bed, new RotationBed());
		RotationManager.registerRotationHandler(Blocks.trapdoor, new RotationTrapdoor());
		RotationManager.registerRotationHandler(Blocks.fence_gate, new RotationFenceGate());
		RotationManager.registerRotationHandler(Blocks.pumpkin, new RotationPumpkin());
		RotationManager.registerRotationHandler(Blocks.lit_pumpkin, new RotationPumpkin());
		RotationManager.registerRotationHandler(Blocks.wooden_button, new RotationButton());
		RotationManager.registerRotationHandler(Blocks.stone_button, new RotationButton());
		RotationManager.registerRotationHandler(Blocks.tripwire_hook, new RotationTripwireSource());
		//TODO Complete door rotation
		//RotationManager.registerRotationHandler(Blocks.doorWood, new RotationDoor());
		//RotationManager.registerRotationHandler(Blocks.doorIron, new RotationDoor());
		//RotationManager.registerRotationHandler(Blocks.pistonBase, new RotationPistonBase());
		//RotationManager.registerRotationHandler(Blocks.pistonStickyBase, new RotationPistonBase());
		RotationManager.registerRotationHandler(Blocks.ladder, new RotationLadder());
		RotationManager.registerRotationHandler(Blocks.torch, new RotationTorch());
		RotationManager.registerRotationHandler(Blocks.unlit_redstone_torch, new RotationTorch());
		RotationManager.registerRotationHandler(Blocks.redstone_torch, new RotationTorch());
		RotationManager.registerRotationHandler(Blocks.unpowered_repeater, new RotationRedstoneRepeater());
		RotationManager.registerRotationHandler(Blocks.powered_repeater, new RotationRedstoneRepeater());
		RotationManager.registerRotationHandler(Blocks.unpowered_comparator, new RotationRedstoneComparator());
		RotationManager.registerRotationHandler(Blocks.powered_comparator, new RotationRedstoneComparator());
		RotationManager.registerRotationHandler(Blocks.lever, new RotationLever());
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
		//TODO FlippedManager.registerFlippedHandler(Block.lever, mew FlippedLever());
		//TODO FlippedManager.registerFlippedHandler(Block.bed, new FlippedBed());
		//TODO Logs, Logs2
	}
}
