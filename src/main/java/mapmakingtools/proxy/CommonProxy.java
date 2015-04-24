package mapmakingtools.proxy;

import java.util.List;

import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.api.manager.ForceKillManager;
import mapmakingtools.api.manager.ItemEditorManager;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.client.gui.GuiWorldTransfer;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.ContainerItemEditor;
import mapmakingtools.container.ContainerWorldTransfer;
import mapmakingtools.tools.RotationLoader;
import mapmakingtools.tools.attribute.ArmorColourAttribute;
import mapmakingtools.tools.attribute.BookAttribute;
import mapmakingtools.tools.attribute.BookEnchantmentAttribute;
import mapmakingtools.tools.attribute.EnchantmentAttribute;
import mapmakingtools.tools.attribute.FireworksAttribute;
import mapmakingtools.tools.attribute.ItemMetaAttribute;
import mapmakingtools.tools.attribute.ItemNameAttribute;
import mapmakingtools.tools.attribute.LoreAttribute;
import mapmakingtools.tools.attribute.PlayerHeadAttribute;
import mapmakingtools.tools.attribute.PotionAttribute;
import mapmakingtools.tools.attribute.RepairCostAttribute;
import mapmakingtools.tools.attribute.StackSizeAttribute;
import mapmakingtools.tools.datareader.BlockList;
import mapmakingtools.tools.datareader.BlockColourList;
import mapmakingtools.tools.datareader.ChestSymmetrifyData;
import mapmakingtools.tools.datareader.EnchantmentList;
import mapmakingtools.tools.datareader.PotionList;
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
import mapmakingtools.tools.killentities.KillAll;
import mapmakingtools.tools.killentities.KillAnimals;
import mapmakingtools.tools.killentities.KillGeneric;
import mapmakingtools.tools.killentities.KillItem;
import mapmakingtools.tools.killentities.KillMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
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
	public static final int GUI_ID_WORLD_TRANSFER = 3;
	
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
		else if(ID == GUI_ID_WORLD_TRANSFER) {
			return new ContainerWorldTransfer();
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
		else if(ID == GUI_ID_WORLD_TRANSFER) {
			return new GuiWorldTransfer();
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
		EnchantmentList.readDataFromFile();
		PotionList.readDataFromFile();
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
	
	public void registerItemAttribute() {}

	public void registerRotation() {
		RotationLoader.loadFiles();
	}

	public void registerForceKill() {
		ForceKillManager.registerHandler("all", new KillAll());
		ForceKillManager.registerHandler("enderman", new KillGeneric(EntityEnderman.class));
		ForceKillManager.registerHandler("blaze", new KillGeneric(EntityBlaze.class));
		ForceKillManager.registerHandler("cow", new KillGeneric(EntityCow.class));
		ForceKillManager.registerHandler("sheep", new KillGeneric(EntitySheep.class));
		ForceKillManager.registerHandler("pig", new KillGeneric(EntityPig.class));
		ForceKillManager.registerHandler("item", new KillItem());
		ForceKillManager.registerHandler("zombie", new KillGeneric(EntityZombie.class));
		ForceKillManager.registerHandler("skeleton", new KillGeneric(EntitySkeleton.class));
		ForceKillManager.registerHandler("pigzombie", new KillGeneric(EntityPigZombie.class));
		ForceKillManager.registerHandler("giant", new KillGeneric(EntityGiantZombie.class));
		ForceKillManager.registerHandler("cavespider", new KillGeneric(EntityCaveSpider.class));
		ForceKillManager.registerHandler("creeper", new KillGeneric(EntityCreeper.class));
		ForceKillManager.registerHandler("ghast", new KillGeneric(EntityGhast.class));
		ForceKillManager.registerHandler("snowman", new KillGeneric(EntitySnowman.class));
		ForceKillManager.registerHandler("irongolem", new KillGeneric(EntityIronGolem.class));
		ForceKillManager.registerHandler("magmacube", new KillGeneric(EntityMagmaCube.class));
		ForceKillManager.registerHandler("silverfish", new KillGeneric(EntitySilverfish.class));
		ForceKillManager.registerHandler("slime", new KillGeneric(EntitySlime.class));
		ForceKillManager.registerHandler("bat", new KillGeneric(EntityBat.class));
		ForceKillManager.registerHandler("chicken", new KillGeneric(EntityChicken.class));
		ForceKillManager.registerHandler("horse", new KillGeneric(EntityHorse.class));
		ForceKillManager.registerHandler("mooshroom", new KillGeneric(EntityMooshroom.class));
		ForceKillManager.registerHandler("monster", new KillMobs());
		ForceKillManager.registerHandler("animal", new KillAnimals());
		
	}
}
