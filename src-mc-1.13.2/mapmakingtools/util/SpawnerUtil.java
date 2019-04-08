package mapmakingtools.util;

import java.lang.reflect.Field;
import java.util.List;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.api.filter.IFilterBase;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedSpawnerEntity;

/**
 * @author ProPercivalalb
 */
public class SpawnerUtil {

	private static Field FIELD_SPAWN_DELAY = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 0);
	private static Field FIELD_POTENTIAL_SPAWNS_LIST = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 1);
	private static Field FIELD_POTENTIAL_SPAWN = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 2);
	private static Field FIELD_MIN_DELAY = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 5);
	private static Field FIELD_MAX_DELAY = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 6);
	private static Field FIELD_SPAWN_COUNT = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 7);
	private static Field FIELD_ENTITY_CAP = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 9);
	private static Field FIELD_DETECTION_RADIUS = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 10);
	private static Field FIELD_SPAWN_RADIUS = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 11);
	
	private static Field FIELD_SPAWNER_LOGIC = ReflectionHelper.getField(EntityMinecartMobSpawner.class, 0);
	
	public static ResourceLocation getMobId(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		if(minecartIndex == -1)
			return getMinecartType(ReflectionHelper.getField(FIELD_POTENTIAL_SPAWN, WeightedSpawnerEntity.class, spawnerLogic));
		else {
			WeightedSpawnerEntity randomMinecart = getPotentialSpawn(spawnerLogic, minecartIndex);
			return getMinecartType(randomMinecart);
		}
	}

	public static void setMobId(MobSpawnerBaseLogic spawnerLogic, String mobId, int minecartIndex) {
		if(minecartIndex == -1) {}
			//ReflectionHelper.setField(mobIdField, spawnerLogic, mobId);
		else {
			confirmHasRandomMinecart(spawnerLogic);
			NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
			tag.putString("id", mobId);
		}
	}
	
	public static void setMinDelay(MobSpawnerBaseLogic spawnerLogic, int minDelay) {
		ReflectionHelper.setField(FIELD_MIN_DELAY, spawnerLogic, minDelay);
		ReflectionHelper.setField(FIELD_SPAWN_DELAY, spawnerLogic, minDelay);
	}
	
	public static void setMaxDelay(MobSpawnerBaseLogic spawnerLogic, int maxDelay) {
		ReflectionHelper.setField(FIELD_MAX_DELAY, spawnerLogic, maxDelay);
	}
	
	public static void setSpawnRadius(MobSpawnerBaseLogic spawnerLogic, int spawnRadius) {
		ReflectionHelper.setField(FIELD_SPAWN_RADIUS, spawnerLogic, spawnRadius);
	}
	
	public static void setSpawnCount(MobSpawnerBaseLogic spawnerLogic, int spawnCount) {
		ReflectionHelper.setField(FIELD_SPAWN_COUNT, spawnerLogic, spawnCount);
	}
	
	public static void setEntityCap(MobSpawnerBaseLogic spawnerLogic, int entityCap) {
		ReflectionHelper.setField(FIELD_ENTITY_CAP, spawnerLogic, entityCap);
	}
	
	public static void setDetectionRadius(MobSpawnerBaseLogic spawnerLogic, int detectionRadius) {
		ReflectionHelper.setField(FIELD_DETECTION_RADIUS, spawnerLogic, detectionRadius);
	}
	
	public static void setItemType(MobSpawnerBaseLogic spawnerLogic, ItemStack item, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		tag.put("Item", item.write(new NBTTagCompound()));
		
		//spawnerLogic.setNextSpawnData(randomMinecart);
	}
	
	public static void setMobArmor(MobSpawnerBaseLogic spawnerLogic, ItemStack helment, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack mainHandItem, ItemStack offHandItem, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		NBTTagList nbttaglist = new NBTTagList();
		
        for(ItemStack itemstack : new ItemStack[] {boots, leggings, chestplate, helment}) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (!itemstack.isEmpty())
                itemstack.write(nbttagcompound);

            nbttaglist.add(nbttagcompound);
        }

        tag.put("ArmorItems", nbttaglist);
		
        NBTTagList nbttaglist1 = new NBTTagList();

        for (ItemStack itemstack1 : new ItemStack[] {mainHandItem, offHandItem}) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            if (!itemstack1.isEmpty())
                itemstack1.write(nbttagcompound1);

            nbttaglist1.add(nbttagcompound1);
        }
        tag.put("HandItems", nbttaglist1);
        
		//spawnerLogic.setNextSpawnData(randomMinecart);
	}
	
	public static ItemStack[] getMobArmor(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		ItemStack[] equipment = new ItemStack[4];
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		if (tag.contains("ArmorItems")) {
			NBTTagList nbttaglist = (NBTTagList)tag.get("ArmorItems");

		    for (int i = 0; i < equipment.length; ++i)
		    	equipment[i] = ItemStack.read(nbttaglist.getCompound(i));
		}
		return equipment;
	}
	
	public static ItemStack[] getMobHeldItems(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		ItemStack[] equipment = new ItemStack[2];
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		if (tag.contains("HandItems")) {
			NBTTagList nbttaglist = (NBTTagList)tag.get("HandItems");

		    for (int i = 0; i < equipment.length; ++i)
		    	equipment[i] = ItemStack.read(nbttaglist.getCompound(i));
		}
		return equipment;
	}
	
	public static void setPosition(MobSpawnerBaseLogic spawnerLogic, double x, double y, double z, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		//if((x == 0.0D && y == 0.0D && z == 0.0D)) {
		//	tag.remove("Pos");
		//}
		tag.put("Pos", newDoubleNBTList(new double[] {x, y, z}));
		//spawnerLogic.setNextSpawnData(randomMinecart);
	}
	
	public static double getPositionX(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		NBTTagList posList = tag.getList("Pos", NBTUtil.ID_DOUBLE);
		return posList.getDouble(0);
	}
	
	public static double getPositionY(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		NBTTagList posList = tag.getList("Pos", NBTUtil.ID_DOUBLE);
		return posList.getDouble(1);
	}
	
	public static double getPositionZ(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		NBTTagList posList = tag.getList("Pos", NBTUtil.ID_DOUBLE);
		return posList.getDouble(2);
	}
	
	public static boolean isSpawnPositionRandom(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = getPotentialSpawn(spawnerLogic, minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		return tag.contains("Pos", 6);
	}
	
	public static void setVelocity(MobSpawnerBaseLogic spawnerLogic, double xMotion, double yMotion, double zMotion, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		tag.put("Motion", newDoubleNBTList(new double[] {xMotion, yMotion, zMotion}));
		//spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static double getMotionX(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		NBTTagList posList = tag.getList("Motion", NBTUtil.ID_DOUBLE);
		return posList.getDouble(0);
	}
	
	public static double getMotionY(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		NBTTagList posList = tag.getList("Motion", NBTUtil.ID_DOUBLE);
		return posList.getDouble(1);
	}
	
	public static double getMotionZ(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		NBTTagList posList = tag.getList("Motion", NBTUtil.ID_DOUBLE);
		return posList.getDouble(2);
	}
	
	public static void setBabyMonster(MobSpawnerBaseLogic spawnerLogic, boolean baby, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		tag.putBoolean("IsBaby", baby);
		//spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static boolean isBabyMonster(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		return tag.getBoolean("IsBaby");
	}
	
	public static void setCreeperFuse(MobSpawnerBaseLogic spawnerLogic, int fuseTime, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		tag.putShort("Fuse", (short)fuseTime);
		//spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static int getCreeperFuse(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		if(!tag.contains("Fuse", 2))
			return 30;
		return tag.getShort("Fuse");
	}

	public static void setCreeperExplosionRadius(MobSpawnerBaseLogic spawnerLogic, int explosionRadius, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		tag.putByte("ExplosionRadius", (byte)explosionRadius);
		//spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static int getExplosionRadius(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		if(!tag.contains("ExplosionRadius", 1))
			return 3;
		return tag.getByte("ExplosionRadius");
	}
	
	public static void setVillagerProfession(MobSpawnerBaseLogic spawnerLogic, int minecartIndex, int professionId) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		tag.putInt("Profession", professionId);
		//spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static int getVillagerProfession(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		NBTTagCompound tag = getMinecartProperties(spawnerLogic, minecartIndex);
		if(!tag.contains("Profession", NBTUtil.ID_INTEGER))
			return 0;
		return tag.getInt("Profession");
	}

	protected static NBTTagList newDoubleNBTList(double ... par1ArrayOfDouble) {
	    NBTTagList nbttaglist = new NBTTagList();
	    double[] adouble = par1ArrayOfDouble;
	    int i = par1ArrayOfDouble.length;

	    for (int j = 0; j < i; ++j) {
	        double d1 = adouble[j];
	        nbttaglist.add(new NBTTagDouble(d1));
	    }

	    return nbttaglist;
	}
	
	public static boolean hasRandomMinecart(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(FIELD_POTENTIAL_SPAWN, WeightedSpawnerEntity.class, spawnerLogic) != null;
	}
	
	public static WeightedSpawnerEntity getRandomMinecart(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(FIELD_POTENTIAL_SPAWN, WeightedSpawnerEntity.class, spawnerLogic);
	}
	
	public static void confirmHasRandomMinecart(MobSpawnerBaseLogic spawnerLogic) {
		List<WeightedSpawnerEntity> minecarts = getPotentialSpawns(spawnerLogic);
		if(minecarts.size() < 1) {

			WeightedSpawnerEntity randomMinecart = new WeightedSpawnerEntity();
			minecarts.add(randomMinecart);
			spawnerLogic.setNextSpawnData(randomMinecart);
		}
		else {
			spawnerLogic.setNextSpawnData(minecarts.get(0));
		}

	}
	
	public static NBTTagCompound getMinecartProperties(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		return getMinecartProperties(getPotentialSpawn(spawnerLogic, minecartIndex));
	}
	
	public static NBTTagCompound getMinecartProperties(WeightedSpawnerEntity minecart) {
		return minecart.getNbt();
	}
	
	public static WeightedSpawnerEntity getPotentialSpawn(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		return getPotentialSpawns(spawnerLogic).get(minecartIndex);
	}
	
	@SuppressWarnings("unchecked")
	public static List<WeightedSpawnerEntity> getPotentialSpawns(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(FIELD_POTENTIAL_SPAWNS_LIST, List.class, spawnerLogic);
	}
	
	/**
	public static void sendSpawnerPacketToAllPlayers(TileEntityMobSpawner spawner) {
		if(!ServerHelper.isServer())
			return;
		MinecraftServer server = MinecraftServer.getServer();
		server.getConfigurationManager().sendPacketToAllPlayersInDimension(getTileEntitySpawnerPacket(spawner), spawner.getWorld().provider.getDimensionId());
	}**/
	
	public static Packet getTileEntitySpawnerPacket(TileEntityMobSpawner spawner) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        spawner.write(nbttagcompound);
        return new SPacketUpdateTileEntity(spawner.getPos(), 1, nbttagcompound);
	}
	
	
	public static ResourceLocation getMinecartType(WeightedSpawnerEntity minecart) {
		String s = minecart.getNbt().getString("id");
        return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
	}
	
	public static int getMinecartWeight(WeightedSpawnerEntity minecart) {
		return minecart.getNbt().getInt("Weight");
	}
	
	public static List<WeightedSpawnerEntity> getPotentialSpawns(IFilterBase gui) {
		return getPotentialSpawns(getSpawnerLogic(gui));
	}
	
	public static MobSpawnerBaseLogic getSpawnerLogic(IFilterBase gui) {
		if(gui.getTargetType() == TargetType.BLOCK) {
			TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
				return spawner.getSpawnerBaseLogic();
			}
		}
		else if(gui.getTargetType() == TargetType.ENTITY) {
			Entity entity = FakeWorldManager.getEntity(gui.getWorld(), gui.getEntityId());
			if(entity instanceof EntityMinecartMobSpawner) {
				EntityMinecartMobSpawner spawner = (EntityMinecartMobSpawner)entity;
				return getLogic(spawner);
			}
		}
		
		return null;
	}
	
	/** Is it a block or minecart spawner */
	public static boolean isSpawner(IFilterBase gui) {
		if(gui.getTargetType() == TargetType.BLOCK) {
			TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
			return tile instanceof TileEntityMobSpawner;
		}
		else if(gui.getTargetType() == TargetType.ENTITY) {
			Entity entity = FakeWorldManager.getEntity(gui.getWorld(), gui.getEntityId());
			return entity instanceof EntityMinecartMobSpawner;
		}
		
		return false;
	}
	
	public static MobSpawnerBaseLogic getLogic(EntityMinecartMobSpawner minecartMobSpawner) {
		return ReflectionHelper.getField(FIELD_SPAWNER_LOGIC, MobSpawnerBaseLogic.class, minecartMobSpawner);
	}
}
