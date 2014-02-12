package mapmakingtools.helper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class SpawnerHelper {
	
	private static int INDEX_MOB_ID    = 1;
	private static int INDEX_MINECART_SPAWN_LIST = 2;
	private static int INDEX_MIN_DELAY = 6;
	private static int INDEX_MAX_DELAY = 7;
	private static int INDEX_SPAWN_COUNT = 8;
	private static int INDEX_PRE_CREATED_ENTITY = 9;
	private static int INDEX_ENTITY_CAP = 10;
	private static int INDEX_DETECTION_RADIUS = 11;
	private static int INDEX_SPAWN_RADIUS = 12;
	
	//Class used to Customise Mob spawners and Minecart spawners.
	
	/**
	 * Gets the Spawner logic, check weather it is null first.
	 * @param targetSpawner The #EntityMinecartMobSpawner or #TileEntitySpawner
	 * @return The Spawner Logic
	 */
	public static MobSpawnerBaseLogic getSpawnerLogic(Object targetSpawner) {
		if(targetSpawner == null) return null; 
		if(targetSpawner instanceof TileEntityMobSpawner) return ((TileEntityMobSpawner)targetSpawner).func_145881_a(); 
		if(targetSpawner instanceof EntityMinecartMobSpawner) return ((EntityMinecartMobSpawner)targetSpawner).func_98039_d();
		return null;
	}
	
	public static Entity getNewEntityFromSpawner(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			return mobSpawnerLogic.func_98281_h();
		}
		return null;
	}
	
	/**
	 * Sends a packet to all players in the spawner dimension to update the 
	 * creature inside the spawner.
	 * Only needs to be used when changing the NBT data of the mob or the mob its self.
	 * @param targetSpawner The #TileEntitySpawner
	 */
	public static void sendSpawnerPacketToAllPlayers(Object targetSpawner) {
		if(targetSpawner instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)targetSpawner;
			MinecraftServer server = MinecraftServer.getServer();
			server.getConfigurationManager().func_148537_a(spawner.func_145844_m(), spawner.func_145831_w().provider.dimensionId);
		}
	}
	
	/**
	 * Sets the spawner minimum delay.
	 * @param tile The #EntityMinecartMobSpawner or #TileEntitySpawner
	 * @param minDelay The new minimum delay
	 */
	public static void setMinDelay(Object tile, int minDelay) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_MIN_DELAY, minDelay);
			mobSpawnerLogic.spawnDelay = minDelay;
		}
		if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
			sendSpawnerPacketToAllPlayers(tile);
		}
	}
	
	/**
	 * Sets the spawner maximum delay.
	 * @param tile The #EntityMinecartMobSpawner or #TileEntitySpawner
	 * @param maxDelay The new maximum delay
	 */
	public static void setMaxDelay(Object tile, int maxDelay) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_MAX_DELAY, maxDelay);
		}
		if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
			sendSpawnerPacketToAllPlayers(tile);
		}
	}
	
	public static void setSpawnRadius(Object tile, int spawnRadius) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_SPAWN_RADIUS, spawnRadius);
		}
		if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
			sendSpawnerPacketToAllPlayers(tile);
		}
	}
	
	public static void setSpawnCount(Object tile, int spawnCount) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_SPAWN_COUNT, spawnCount);
		}
		if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
			sendSpawnerPacketToAllPlayers(tile);
		}
	}
	
	public static void setEntityCap(Object tile, int entityCap) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_ENTITY_CAP, entityCap);
		}
		if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
			sendSpawnerPacketToAllPlayers(tile);
		}
	}
	
	public static void setDetectionRadius(Object tile, int detectionRadius) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_DETECTION_RADIUS, detectionRadius);
		}
		if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
			sendSpawnerPacketToAllPlayers(tile);
		}
	}
	
	/**
	 * Sets the Mob ID
	 * @param tile The #EntityMinecartMobSpawner or #TileEntitySpawner
	 * @param mobId The new maximum delay
	 */
	public static void setMobId(Object tile, String mobId) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		int minDelay = getMinDelay(tile);
		int maxDelay = getMaxDelay(tile);
		int spawnCount = getSpawnCount(tile);
		int spawnRadius = getSpawnRadius(tile);
		int detectionRange = getDetectionRange(tile);
		int entityCap = getEntityCap(tile);
		TileEntityMobSpawner newSpawner = null;
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			spawner.func_145831_w().setTileEntity(spawner.field_145851_c, spawner.field_145848_d, spawner.field_145849_e, newSpawner = new TileEntityMobSpawner());
			mobSpawnerLogic = getSpawnerLogic(newSpawner);
		}
		if(mobSpawnerLogic != null) { 
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_MOB_ID, mobId);
				
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_MIN_DELAY, minDelay);
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_MAX_DELAY, maxDelay);
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_SPAWN_COUNT, spawnCount);
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_SPAWN_RADIUS, spawnRadius);
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_DETECTION_RADIUS, detectionRange);
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_ENTITY_CAP, entityCap);
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers((newSpawner == null ? tile : newSpawner));
			}
		}
	}
	
	public static void setWeightedMinecart(Object tile, List<WeightedRandomMinecart> list) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			ReflectionHelper.setField(MobSpawnerBaseLogic.class, mobSpawnerLogic, INDEX_MINECART_SPAWN_LIST, list);
		}
	}
	
	public static WeightedRandomMinecart getWeightedMinecart(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null && mobSpawnerLogic.getRandomMinecart() != null) { 
			return mobSpawnerLogic.getRandomMinecart();
		}
		try {
			return WeightedRandomMinecart.class.getConstructor(NBTTagCompound.class, String.class).newInstance(new NBTTagCompound(), getMobId(tile));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int getMaxDelay(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			return ReflectionHelper.getField(MobSpawnerBaseLogic.class, Integer.class, mobSpawnerLogic, INDEX_MAX_DELAY);
		}
		return 800;
	}
	
	public static int getMinDelay(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			return ReflectionHelper.getField(MobSpawnerBaseLogic.class, Integer.class, mobSpawnerLogic, INDEX_MIN_DELAY);
		}
		return 200;
	}
	
	public static int getSpawnCount(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			return ReflectionHelper.getField(MobSpawnerBaseLogic.class, Integer.class, mobSpawnerLogic, INDEX_SPAWN_COUNT);
		}
		return 4;
	}
	
	public static int getSpawnRadius(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			return ReflectionHelper.getField(MobSpawnerBaseLogic.class, Integer.class, mobSpawnerLogic, INDEX_SPAWN_RADIUS);
		}
		return 4;
	}
	
	public static int getDetectionRange(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			return ReflectionHelper.getField(MobSpawnerBaseLogic.class, Integer.class, mobSpawnerLogic, INDEX_DETECTION_RADIUS);
		}
		return 16;
	}
	
	public static int getEntityCap(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			return ReflectionHelper.getField(MobSpawnerBaseLogic.class, Integer.class, mobSpawnerLogic, INDEX_ENTITY_CAP);
		}
		return 6;
	}
	
	//Getter
	public static String getMobId(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				return mobSpawnerLogic.getRandomMinecart().minecartName;
			}
			return ReflectionHelper.getField(MobSpawnerBaseLogic.class, String.class, mobSpawnerLogic, INDEX_MOB_ID);
		}
		return "Pig";
	}
	
	//Other more in detail methods
	public static void setToBabyMonster(Object tile, boolean isChild) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
			tag.setBoolean("IsBaby", isChild);
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	public static boolean getBabyMonster(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				return mobSpawnerLogic.getRandomMinecart().field_98222_b.getBoolean("IsBaby");
			}
		}
		return false;
	}
	
	public static void setCreeperFuse(Object tile, int fuseTime) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
			tag.setShort("Fuse", (short)fuseTime);
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	public static int getCreeperFuse(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				return (int)mobSpawnerLogic.getRandomMinecart().field_98222_b.getShort("Fuse");
			}
		}
		return 30;
	}

	public static void setCreeperExplosionRadius(Object tile, int explosionRadius) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
			tag.setByte("ExplosionRadius", (byte)explosionRadius);
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	public static int getCreeperExplosionRadius(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				return (int)mobSpawnerLogic.getRandomMinecart().field_98222_b.getByte("ExplosionRadius");
			}
		}
		return 3;
	}

	public static void setMotion(Object tile, double motionX, double motionY, double motionZ) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
			tag.setTag("Motion", newDoubleNBTList(new double[] {motionX, motionY, motionZ}));
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	public static double getMotionX(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagList nbttaglist1 =  mobSpawnerLogic.getRandomMinecart().field_98222_b.getTagList("Motion");
				if(nbttaglist1.tagCount() < 1) return 0.0D;
				NBTTagDouble doub = (NBTTagDouble)nbttaglist1.tagAt(0);
				if(doub != null) {
					return doub.data;
				}
			}
		}
		return 0.0D;
	}
	
	public static double getMotionY(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagList nbttaglist1 =  mobSpawnerLogic.getRandomMinecart().field_98222_b.getTagList("Motion");
				if(nbttaglist1.tagCount() < 2) return 0.0D;
				NBTTagDouble doub = (NBTTagDouble)nbttaglist1.tagAt(1);
				if(doub != null) {
					return doub.data;
				}
			}
		}
		return 0.0D;
	}
	
	public static double getMotionZ(Object tile) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagList nbttaglist1 =  mobSpawnerLogic.getRandomMinecart().field_98222_b.getTagList("Motion");
				if(nbttaglist1.tagCount() < 3) return 0.0D;
				NBTTagDouble doub = (NBTTagDouble)nbttaglist1.tagAt(2);
				if(doub != null) {
					return doub.data;
				}
			}
		}
		return 0.0D;
	}
	
	public static void setPosition(Object tile, double x, double y, double z) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
			if((x == 0.0D && y == 0.0D && z == 0.0D)) {
				tag.removeTag("Pos");
			}
			else {
				tag.setTag("Pos", newDoubleNBTList(new double[] {x, y, z}));
			}
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	public static double getPositionX(Object tile, int _default) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagList nbttaglist1 =  mobSpawnerLogic.getRandomMinecart().field_98222_b.getTagList("Pos");
				if(nbttaglist1.tagCount() < 1) return _default;
				NBTTagDouble doub = (NBTTagDouble)nbttaglist1.tagAt(0);
				if(doub != null) {
					return doub.data;
				}
			}
		}
		return _default;
	}

	public static double getPositionY(Object tile, int _default) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagList nbttaglist1 =  mobSpawnerLogic.getRandomMinecart().field_98222_b.getTagList("Pos");
				if(nbttaglist1.tagCount() < 2) return _default;
				NBTTagDouble doub = (NBTTagDouble)nbttaglist1.tagAt(1);
				if(doub != null) {
					return doub.data;
				}
			}
		}
		return _default;
	}
	
	public static double getPositionZ(Object tile, int _default) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagList nbttaglist1 =  mobSpawnerLogic.getRandomMinecart().field_98222_b.getTagList("Pos");
				if(nbttaglist1.tagCount() < 3) return _default;
				NBTTagDouble doub = (NBTTagDouble)nbttaglist1.tagAt(2);
				if(doub != null) {
					return doub.data;
				}
			}
		}
		return _default;
	}
	
	public static void setMobArmor(Object tile, ItemStack helment, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack heldItem) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
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
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	/**
	 * 
	 * @param targetSpawner The #EntityMinecartMobSpawner or #TileEntitySpawner
	 * @return Equipment with [0] = Held Item, [1] = Boots, [2] = Leggings, [3] = Chestplate, [4] = Helmet
	 */
	public static ItemStack[] getMobArmor(Object targetSpawner) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(targetSpawner);
		ItemStack[] equipment = new ItemStack[5];
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagCompound tag =  mobSpawnerLogic.getRandomMinecart().field_98222_b;
				if (tag.hasKey("Equipment")) {
		            NBTTagList nbttaglist = tag.getTagList("Equipment");

		            for (int i = 0; i < equipment.length; ++i) {
		                equipment[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbttaglist.tagAt(i));
		            }
		        }
			}
		}
		return equipment;
	}
	
	public static void setPotionType(Object tile, ItemStack potion) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
			LogHelper.logDebug("set Potion");
		    tag.setCompoundTag("Potion", potion == null ? new NBTTagCompound() : potion.writeToNBT(new NBTTagCompound()));
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	public static ItemStack getPotionType(Object targetSpawner) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(targetSpawner);
		ItemStack potion = null;
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagCompound tag =  mobSpawnerLogic.getRandomMinecart().field_98222_b;
				if (tag.hasKey("Potion")) {
		            potion = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Potion"));
		        }
			}
		}
		return potion;
	}
	
	public static void setItemType(Object tile, ItemStack potion) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(tile);
		if(mobSpawnerLogic != null) {
			NBTTagCompound tag = new NBTTagCompound();
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				tag = mobSpawnerLogic.getRandomMinecart().field_98222_b;
			}
			if(potion == null) {
				potion = new ItemStack(Block.stone);
			}
		    tag.setCompoundTag("Item", potion == null ? new NBTTagCompound() : potion.writeToNBT(new NBTTagCompound()));
			if(mobSpawnerLogic.getRandomMinecart() == null) {
				mobSpawnerLogic.setRandomMinecart(new WeightedRandomMinecart(mobSpawnerLogic, tag, getMobId(tile)));
			}
			if(!mobSpawnerLogic.getSpawnerWorld().isRemote) {
				sendSpawnerPacketToAllPlayers(tile);
			}
		}
	}
	
	public static ItemStack getItemType(Object targetSpawner) {
		MobSpawnerBaseLogic mobSpawnerLogic = getSpawnerLogic(targetSpawner);
		ItemStack item = null;
		if(mobSpawnerLogic != null) { 
			if(mobSpawnerLogic.getRandomMinecart() != null) {
				NBTTagCompound tag =  mobSpawnerLogic.getRandomMinecart().field_98222_b;
				if (tag.hasKey("Item")) {
		            item = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Item"));
		        }
			}
		}
		return item;
	}
	
	protected static NBTTagList newDoubleNBTList(double ... par1ArrayOfDouble) {
	    NBTTagList nbttaglist = new NBTTagList();
	    double[] adouble = par1ArrayOfDouble;
	    int i = par1ArrayOfDouble.length;

	    for (int j = 0; j < i; ++j) {
	        double d1 = adouble[j];
	        nbttaglist.appendTag(new NBTTagDouble((String)null, d1));
	    }

	    return nbttaglist;
	}
}
