package mapmakingtools.util;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.Lists;

import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class SpawnerUtil {

	private static Field minDelayField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 5);
	private static Field maxDelayField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 6);
	private static Field spawnDelayField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 0);
	private static Field spawnRadiusField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 11);
	private static Field spawnCountField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 7);
	private static Field entityCapField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 9);
	private static Field detectionRadiusField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 10);
	private static Field potentialSpawnsListField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 1);
	private static Field randomMinecartField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 2);
	
	public static ResourceLocation getMobId(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		if(minecartIndex == -1)
			return getMinecartType(ReflectionHelper.getField(randomMinecartField, WeightedSpawnerEntity.class, spawnerLogic));
		else {
			WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
			return getMinecartType(randomMinecart);
		}
	}

	public static void setMobId(MobSpawnerBaseLogic spawnerLogic, String mobId, int minecartIndex) {
		if(minecartIndex == -1) {}
			//ReflectionHelper.setField(mobIdField, spawnerLogic, mobId);
		else {
			confirmHasRandomMinecart(spawnerLogic);
			WeightedSpawnerEntity randomMinecart = getPotentialSpawns(spawnerLogic).get(minecartIndex);
			NBTTagCompound data = randomMinecart.getNbt();
			data.setString("id", mobId);
			getPotentialSpawns(spawnerLogic).set(minecartIndex, randomMinecart);
			spawnerLogic.setNextSpawnData(randomMinecart);
		}
	}
	
	public static void setMinDelay(MobSpawnerBaseLogic spawnerLogic, int minDelay) {
		ReflectionHelper.setField(minDelayField, spawnerLogic, minDelay);
		ReflectionHelper.setField(spawnDelayField, spawnerLogic, minDelay);
	}
	
	public static void setMaxDelay(MobSpawnerBaseLogic spawnerLogic, int maxDelay) {
		ReflectionHelper.setField(maxDelayField, spawnerLogic, maxDelay);
	}
	
	public static void setSpawnRadius(MobSpawnerBaseLogic spawnerLogic, int spawnRadius) {
		ReflectionHelper.setField(spawnRadiusField, spawnerLogic, spawnRadius);
	}
	
	public static void setSpawnCount(MobSpawnerBaseLogic spawnerLogic, int spawnCount) {
		ReflectionHelper.setField(spawnCountField, spawnerLogic, spawnCount);
	}
	
	public static void setEntityCap(MobSpawnerBaseLogic spawnerLogic, int entityCap) {
		ReflectionHelper.setField(entityCapField, spawnerLogic, entityCap);
	}
	
	public static void setDetectionRadius(MobSpawnerBaseLogic spawnerLogic, int detectionRadius) {
		ReflectionHelper.setField(detectionRadiusField, spawnerLogic, detectionRadius);
	}
	
	public static void setItemType(MobSpawnerBaseLogic spawnerLogic, ItemStack item, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		tag.setTag("Item", item.writeToNBT(new NBTTagCompound()));
		
		spawnerLogic.setNextSpawnData(randomMinecart);
	}
	
	public static void setMobArmor(MobSpawnerBaseLogic spawnerLogic, ItemStack helment, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack heldItem, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		
		NBTTagList nbttaglist = new NBTTagList();
		NBTTagCompound nbttagcompound1;
		//Held Item
		nbttagcompound1 = new NBTTagCompound();
		if(heldItem != null) heldItem.writeToNBT(nbttagcompound1);
		nbttaglist.appendTag(nbttagcompound1);
		//Boots
		nbttagcompound1 = new NBTTagCompound();
		if(boots != null) boots.writeToNBT(nbttagcompound1);
		nbttaglist.appendTag(nbttagcompound1);
		//Leggings
		nbttagcompound1 = new NBTTagCompound();
		if(leggings != null) leggings.writeToNBT(nbttagcompound1);
		nbttaglist.appendTag(nbttagcompound1);
		//Chest
		nbttagcompound1 = new NBTTagCompound();
		if(chestplate != null) chestplate.writeToNBT(nbttagcompound1);
		nbttaglist.appendTag(nbttagcompound1);
		//Helmet
		nbttagcompound1 = new NBTTagCompound();
		if(helment != null) helment.writeToNBT(nbttagcompound1);
		nbttaglist.appendTag(nbttagcompound1);
		tag.setTag("Equipment", nbttaglist);
		spawnerLogic.setNextSpawnData(randomMinecart);
	}
	
	public static ItemStack[] getMobArmor(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		ItemStack[] equipment = new ItemStack[5];
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		if (tag.hasKey("Equipment")) {
			NBTTagList nbttaglist = (NBTTagList)tag.getTag("Equipment");

		    for (int i = 0; i < equipment.length; ++i)
		    	equipment[i] = new ItemStack(nbttaglist.getCompoundTagAt(i));
		}
		return equipment;
	}
	
	public static void setPosition(MobSpawnerBaseLogic spawnerLogic, double x, double y, double z, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		//if((x == 0.0D && y == 0.0D && z == 0.0D)) {
		//	tag.removeTag("Pos");
		//}
		tag.setTag("Pos", newDoubleNBTList(new double[] {x, y, z}));
		spawnerLogic.setNextSpawnData(randomMinecart);
	}
	
	public static double getPositionX(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		NBTTagList posList = tag.getTagList("Pos", 6);
		return posList.getDoubleAt(0);
	}
	
	public static double getPositionY(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		NBTTagList posList = tag.getTagList("Pos", 6);
		return posList.getDoubleAt(1);
	}
	
	public static double getPositionZ(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		NBTTagList posList = tag.getTagList("Pos", 6);
		return posList.getDoubleAt(2);
	}
	
	public static boolean isSpawnPositionRandom(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		return tag.hasKey("Pos", 6);
	}
	
	public static void setVelocity(MobSpawnerBaseLogic spawnerLogic, double xMotion, double yMotion, double zMotion, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		tag.setTag("Motion", newDoubleNBTList(new double[] {xMotion, yMotion, zMotion}));
		spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static double getMotionX(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		NBTTagList posList = tag.getTagList("Motion", 6);
		return posList.getDoubleAt(0);
	}
	
	public static double getMotionY(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		NBTTagList posList = tag.getTagList("Motion", 6);
		return posList.getDoubleAt(1);
	}
	
	public static double getMotionZ(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		NBTTagList posList = tag.getTagList("Motion", 6);
		return posList.getDoubleAt(2);
	}
	
	public static void setBabyMonster(MobSpawnerBaseLogic spawnerLogic, boolean baby, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		tag.setBoolean("IsBaby", baby);
		spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static boolean isBabyMonster(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		return tag.getBoolean("IsBaby");
	}
	
	public static void setCreeperFuse(MobSpawnerBaseLogic spawnerLogic, int fuseTime, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		tag.setShort("Fuse", (short)fuseTime);
		spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static int getCreeperFuse(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		if(!tag.hasKey("Fuse", 2))
			return 30;
		return tag.getShort("Fuse");
	}

	public static void setCreeperExplosionRadius(MobSpawnerBaseLogic spawnerLogic, int explosionRadius, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		tag.setByte("ExplosionRadius", (byte)explosionRadius);
		spawnerLogic.setNextSpawnData(randomMinecart);	
	}
	
	public static int getExplosionRadius(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		WeightedSpawnerEntity randomMinecart = (WeightedSpawnerEntity)ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = getMinecartProperties(randomMinecart);
		if(!tag.hasKey("ExplosionRadius", 1))
			return 3;
		return tag.getByte("ExplosionRadius");
	}

	protected static NBTTagList newDoubleNBTList(double ... par1ArrayOfDouble) {
	    NBTTagList nbttaglist = new NBTTagList();
	    double[] adouble = par1ArrayOfDouble;
	    int i = par1ArrayOfDouble.length;

	    for (int j = 0; j < i; ++j) {
	        double d1 = adouble[j];
	        nbttaglist.appendTag(new NBTTagDouble(d1));
	    }

	    return nbttaglist;
	}
	
	public static boolean hasRandomMinecart(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(randomMinecartField, WeightedSpawnerEntity.class, spawnerLogic) != null;
	}
	
	public static WeightedSpawnerEntity getRandomMinecart(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(randomMinecartField, WeightedSpawnerEntity.class, spawnerLogic);
	}
	
	public static void confirmHasRandomMinecart(MobSpawnerBaseLogic spawnerLogic) {
		List<WeightedSpawnerEntity> minecarts = getPotentialSpawns(spawnerLogic);
		if(minecarts.size() < 1) {
			FMLLog.info("Add Minecart");

			WeightedSpawnerEntity randomMinecart = new WeightedSpawnerEntity();
			minecarts.add(randomMinecart);
			spawnerLogic.setNextSpawnData(randomMinecart);
		}
		else {
			spawnerLogic.setNextSpawnData(minecarts.get(0));
		}

	}
	
	public static List<WeightedSpawnerEntity> getPotentialSpawns(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(potentialSpawnsListField, List.class, spawnerLogic);
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
        spawner.writeToNBT(nbttagcompound);
        return new SPacketUpdateTileEntity(spawner.getPos(), 1, nbttagcompound);
	}
	
	public static NBTTagCompound getMinecartProperties(WeightedSpawnerEntity minecart) {
		return minecart.getNbt().getCompoundTag("Entity");
	}
	
	public static ResourceLocation getMinecartType(WeightedSpawnerEntity minecart) {
		String s = minecart.getNbt().getString("id");
        return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
	}
	
	public static int getMinecartWeight(WeightedSpawnerEntity minecart) {
		return minecart.getNbt().getInteger("Weight");
	}
}
