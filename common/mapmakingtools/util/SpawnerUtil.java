package mapmakingtools.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLLog;

import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.helper.ServerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.tileentity.TileEntityMobSpawner;

/**
 * @author ProPercivalalb
 */
public class SpawnerUtil {

	private static Field mobIdField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 1);
	private static Field randomMinecartListField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 2);
	
	public static String getMobId(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		return ReflectionHelper.getField(mobIdField, String.class, spawnerLogic);
	}

	public static void setMobId(MobSpawnerBaseLogic spawnerLogic, String mobId, int minecartIndex) {
		if(minecartIndex == -1)
			ReflectionHelper.setField(mobIdField, spawnerLogic, mobId);
		else {
			confirmHasRandomMinecart(spawnerLogic);
			WeightedRandomMinecart randomMinecart = (WeightedRandomMinecart)ReflectionHelper.getField(randomMinecartListField, List.class, spawnerLogic).get(minecartIndex);
			NBTTagCompound data = randomMinecart.func_98220_a();
			data.setString("Type", mobId);
			WeightedRandomMinecart newRandomMinecart = spawnerLogic.new WeightedRandomMinecart(data);
			ReflectionHelper.getField(randomMinecartListField, List.class, spawnerLogic).set(minecartIndex, newRandomMinecart);
			spawnerLogic.setRandomMinecart(newRandomMinecart);
		}
	}
	
	public static void setMobArmor(MobSpawnerBaseLogic spawnerLogic, ItemStack helment, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack heldItem, int minecartIndex) {
		WeightedRandomMinecart randomMinecart = (WeightedRandomMinecart)ReflectionHelper.getField(randomMinecartListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = randomMinecart.field_98222_b;
		
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
		spawnerLogic.setRandomMinecart(randomMinecart);
	}
	
	public static ItemStack[] getMobArmor(MobSpawnerBaseLogic spawnerLogic, int minecartIndex) {
		ItemStack[] equipment = new ItemStack[5];
		WeightedRandomMinecart randomMinecart = (WeightedRandomMinecart)ReflectionHelper.getField(randomMinecartListField, List.class, spawnerLogic).get(minecartIndex);
		NBTTagCompound tag = randomMinecart.field_98222_b;
		if (tag.hasKey("Equipment")) {
			NBTTagList nbttaglist = (NBTTagList)tag.getTag("Equipment");

		    for (int i = 0; i < equipment.length; ++i) {
		    	equipment[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbttaglist.func_150305_b(i));
		    }
		}
		return equipment;
	}
	
	public static void confirmHasRandomMinecart(MobSpawnerBaseLogic spawnerLogic) {
		if(spawnerLogic.getRandomMinecart() != null) {
			FMLLog.info("Has Random minecart");
			return;
		}
		
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("Weight", 1);
		data.setString("Type", getMobId(spawnerLogic, -1));
		data.setTag("Properties", new NBTTagCompound());
		WeightedRandomMinecart randomMinecart = spawnerLogic.new WeightedRandomMinecart(data);
		spawnerLogic.setRandomMinecart(randomMinecart);
		ReflectionHelper.setField(randomMinecartListField, spawnerLogic, new ArrayList());
		ReflectionHelper.getField(randomMinecartListField, List.class, spawnerLogic).add(randomMinecart);
	}
	
	public static List<WeightedRandomMinecart> getRandomMinecarts(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(randomMinecartListField, List.class, spawnerLogic);
	}
	
	public static void sendSpawnerPacketToAllPlayers(TileEntityMobSpawner spawner) {
		if(!ServerHelper.isServer())
			return;
		MinecraftServer server = MinecraftServer.getServer();
		server.getConfigurationManager().func_148537_a(func_145844_m(spawner), spawner.func_145831_w().provider.dimensionId);
	}
	
	public static Packet func_145844_m(TileEntityMobSpawner spawner) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        spawner.func_145841_b(nbttagcompound);
        return new S35PacketUpdateTileEntity(spawner.field_145851_c, spawner.field_145848_d, spawner.field_145849_e, 1, nbttagcompound);
    }

}
