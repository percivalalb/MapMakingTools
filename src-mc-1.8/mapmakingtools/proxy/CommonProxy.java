package mapmakingtools.proxy;

import java.util.List;

import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.api.manager.ForceKillManager;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.ContainerItemEditor;
import mapmakingtools.container.ContainerWorldTransfer;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.tools.RotationLoader;
import mapmakingtools.tools.datareader.BlockColourList;
import mapmakingtools.tools.datareader.BlockList;
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
import mapmakingtools.tools.filter.EditSignClientFilter;
import mapmakingtools.tools.filter.EditSignServerFilter;
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
import mapmakingtools.tools.filter.VillagerProfessionClientFilter;
import mapmakingtools.tools.filter.VillagerProfessionServerFilter;
import mapmakingtools.tools.filter.VillagerShopClientFilter;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
import mapmakingtools.tools.killentities.KillAll;
import mapmakingtools.tools.killentities.KillAnimals;
import mapmakingtools.tools.killentities.KillGeneric;
import mapmakingtools.tools.killentities.KillItem;
import mapmakingtools.tools.killentities.KillMobs;
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
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
		BlockPos pos = new BlockPos(x, y, z);
		
		if(ID == ID_FILTER_BLOCK) {
			List<IFilterServer> filterList = FilterManager.getServerBlocksFilters(player, world, pos);
			if(filterList.size() > 0)
				return new ContainerFilter(filterList, player).setBlockPos(pos);
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
		return null;
	}

	public void registerHandlers() {}
	
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}
	
	public EntityPlayer getPlayerEntity() {
		return null;
	}
	
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.getServerForPlayer();
	}
	
	public void onPreLoad() {
		BlockList.readDataFromFile();
		BlockColourList.readDataFromFile();
		SpawnerEntitiesList.readDataFromFile();
		ChestSymmetrifyData.readDataFromFile();
		EnchantmentList.readDataFromFile();
		PotionList.readDataFromFile();
	}
	
	public void onPostLoad() {}
	
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
    	
    	FilterManager.registerFilter(EditSignClientFilter.class, EditSignServerFilter.class);
    	
    	FilterManager.registerFilter(VillagerShopClientFilter.class, VillagerShopServerFilter.class);
    	FilterManager.registerFilter(VillagerProfessionClientFilter.class, VillagerProfessionServerFilter.class);
    	
    	FilterManager.registerProvider(SpawnerFilterProvider.class);
	}
	
	public void registerItemAttribute() {}

	public void registerRotation() {
		RotationLoader.clear();
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-acaciadoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-acaciafencegate.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-acaciastairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-anvil.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-bannerstanding.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-bannerwall.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-bed.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-birchdoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-birchfencegate.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-birchstairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-brickstairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-chest.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-cooblestonestairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-darkoakdoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-darkoakfencegate.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-darkoakstairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-dispenser.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-dropper.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-enderchest.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-haybale.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-hopper.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-irondoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-irontrapdoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-jungledoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-junglefencegate.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-junglestairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-ladder.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-lever.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-litfurnace.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-litpumpkin.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-log.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-log2.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-netherbrickstairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-oakdoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-oakfencegate.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-oakstairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-pistion.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-pistionhead.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-quartzpillar.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-quartzstairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-rail.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-railactivator.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-raildetector.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-railpowered.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-redstonecomparator.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-redstonerepeateroff.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-redstonerepeateron.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-redstonetorch.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-sandstonestairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-signstanding.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-signwall.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-sprucedoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-sprucefencegate.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-sprucestairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-stickypistion.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-stonebrickstairs.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-stonebutton.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-stoneslab.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-stoneslab2.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-torch.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-trapdoor.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-trappedchest.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-tripwiresource.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-unlitfurnace.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-unlitpumpkin.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-woodenbutton.txt");
		RotationLoader.loadFromTextFile("/assets/mapmakingtools/data/rotation/minecraft-woodenslab.txt");
	}

	public void registerForceKill() {
		ForceKillManager.registerHandler("all", new KillAll());
		ForceKillManager.registerHandler("arrow", new KillGeneric(EntityArrow.class));
		//ForceKillManager.registerHandler("fireb", new KillGeneric(EntityFireball.class));
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
		ForceKillManager.registerHandler("rabbit", new KillGeneric(EntityRabbit.class));
		ForceKillManager.registerHandler("wolf", new KillGeneric(EntityWolf.class));
		ForceKillManager.registerHandler("villager", new KillGeneric(EntityVillager.class));
		ForceKillManager.registerHandler("monster", new KillMobs());
		ForceKillManager.registerHandler("animal", new KillAnimals());
		
	}
}
